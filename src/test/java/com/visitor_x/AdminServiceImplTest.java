package com.visitor_x;

import com.visitor_x.dto.AdminRequestDTO;
import com.visitor_x.dto.AdminResponseDTO;
import com.visitor_x.dto.ChangePasswordRequestDTO;
import com.visitor_x.entity.Admin;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.AdminRepository;
import com.visitor_x.serviceImpl.AdminServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    @Test
    void createAdmin_Success() {

        AdminRequestDTO request = new AdminRequestDTO();
        request.setUsername("admin");
        request.setPassword("password123");

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123"))
                .thenReturn("encodedPassword");

        Admin savedAdmin = Admin.builder()
                .adminId(1L)
                .username("admin")
                .password("encodedPassword")
                .role("ADMIN")
                .build();

        when(adminRepository.save(any(Admin.class)))
                .thenReturn(savedAdmin);

        AdminResponseDTO response =
                adminService.createAdmin(request);

        assertNotNull(response);
        assertEquals(1L, response.getAdminId());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void createAdmin_DuplicateUsername() {

        Admin existingAdmin = Admin.builder()
                .username("admin")
                .build();

        AdminRequestDTO request = new AdminRequestDTO();
        request.setUsername("admin");

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(existingAdmin));

        assertThrows(
                DuplicateResourceException.class,
                () -> adminService.createAdmin(request)
        );
    }

    @Test
    void getAdmin_Success() {

        Admin admin = Admin.builder()
                .adminId(1L)
                .username("admin")
                .role("ADMIN")
                .build();

        when(adminRepository.findById(1L))
                .thenReturn(Optional.of(admin));

        AdminResponseDTO response =
                adminService.getAdmin(1L);

        assertEquals("admin", response.getUsername());
    }

    @Test
    void getAdmin_NotFound() {

        when(adminRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.getAdmin(1L)
        );
    }

    @Test
    void getAllAdmins_Success() {

        Admin admin1 = Admin.builder()
                .adminId(1L)
                .username("admin1")
                .role("ADMIN")
                .build();

        Admin admin2 = Admin.builder()
                .adminId(2L)
                .username("admin2")
                .role("ADMIN")
                .build();

        when(adminRepository.findAll())
                .thenReturn(List.of(admin1, admin2));

        List<AdminResponseDTO> response =
                adminService.getAllAdmins();

        assertEquals(2, response.size());
    }

    @Test
    void deleteAdmin_Success() {

        when(adminRepository.existsById(1L))
                .thenReturn(true);

        when(adminRepository.count())
                .thenReturn(2L);

        adminService.deleteAdmin(1L);

        verify(adminRepository)
                .deleteById(1L);
    }

    @Test
    void deleteAdmin_NotFound() {

        when(adminRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.deleteAdmin(1L)
        );
    }

    @Test
    void deleteAdmin_LastAdmin() {

        when(adminRepository.existsById(1L))
                .thenReturn(true);

        when(adminRepository.count())
                .thenReturn(1L);

        assertThrows(
                IllegalStateException.class,
                () -> adminService.deleteAdmin(1L)
        );
    }

    @Test
    void changePassword_Success() {

        Admin admin = Admin.builder()
                .adminId(1L)
                .username("admin")
                .password("encodedOldPassword")
                .build();

        ChangePasswordRequestDTO request =
                new ChangePasswordRequestDTO();

        request.setOldPassword("oldPassword");
        request.setNewPassword("newPassword");

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        when(passwordEncoder.matches(
                "oldPassword",
                "encodedOldPassword"))
                .thenReturn(true);

        when(passwordEncoder.encode("newPassword"))
                .thenReturn("encodedNewPassword");

        adminService.changePassword("admin", request);

        verify(adminRepository)
                .save(any(Admin.class));
    }

    @Test
    void changePassword_AdminNotFound() {

        ChangePasswordRequestDTO request =
                new ChangePasswordRequestDTO();

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.changePassword(
                        "admin",
                        request)
        );
    }

    @Test
    void changePassword_WrongOldPassword() {

        Admin admin = Admin.builder()
                .username("admin")
                .password("encodedPassword")
                .build();

        ChangePasswordRequestDTO request =
                new ChangePasswordRequestDTO();

        request.setOldPassword("wrongPassword");

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        when(passwordEncoder.matches(
                "wrongPassword",
                "encodedPassword"))
                .thenReturn(false);

        assertThrows(
                IllegalArgumentException.class,
                () -> adminService.changePassword(
                        "admin",
                        request)
        );
    }
}