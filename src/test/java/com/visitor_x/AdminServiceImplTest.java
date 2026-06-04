package com.visitor_x;

import com.visitor_x.dto.AdminRequestDTO;
import com.visitor_x.dto.AdminResponseDTO;
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
    void createAdmin_ShouldCreateSuccessfully() {

        AdminRequestDTO request = new AdminRequestDTO();
        request.setUsername("admin");
        request.setPassword("admin123");

        Admin savedAdmin = Admin.builder()
                .adminId(1L)
                .username("admin")
                .password("encodedPassword")
                .role("ADMIN")
                .build();

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("admin123"))
                .thenReturn("encodedPassword");

        when(adminRepository.save(any(Admin.class)))
                .thenReturn(savedAdmin);

        AdminResponseDTO response =
                adminService.createAdmin(request);

        assertNotNull(response);
        assertEquals(1L, response.getAdminId());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());

        verify(passwordEncoder).encode("admin123");
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void createAdmin_ShouldThrowException_WhenUsernameExists() {

        AdminRequestDTO request = new AdminRequestDTO();
        request.setUsername("admin");

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(new Admin()));

        assertThrows(
                DuplicateResourceException.class,
                () -> adminService.createAdmin(request)
        );

        verify(adminRepository, never()).save(any());
    }

    @Test
    void getAdmin_ShouldReturnAdmin_WhenIdExists() {

        Long id = 1L;

        Admin admin = Admin.builder()
                .adminId(id)
                .username("admin")
                .role("ADMIN")
                .build();

        when(adminRepository.findById(id))
                .thenReturn(Optional.of(admin));

        AdminResponseDTO response =
                adminService.getAdmin(id);

        assertEquals(id, response.getAdminId());
        assertEquals("admin", response.getUsername());
    }

    @Test
    void getAdmin_ShouldThrowException_WhenIdNotFound() {

        when(adminRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.getAdmin(1L)
        );
    }

    @Test
    void getAllAdmins_ShouldReturnAdminList() {

        List<Admin> admins = List.of(
                Admin.builder()
                        .adminId(1L)
                        .username("admin1")
                        .role("ADMIN")
                        .build(),
                Admin.builder()
                        .adminId(2L)
                        .username("admin2")
                        .role("ADMIN")
                        .build()
        );

        when(adminRepository.findAll())
                .thenReturn(admins);

        List<AdminResponseDTO> result =
                adminService.getAllAdmins();

        assertEquals(2, result.size());
    }

    @Test
    void getAllAdmins_ShouldReturnEmptyList() {

        when(adminRepository.findAll())
                .thenReturn(List.of());

        List<AdminResponseDTO> result =
                adminService.getAllAdmins();

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAdmin_ShouldDeleteSuccessfully() {

        Long id = 1L;

        when(adminRepository.existsById(id))
                .thenReturn(true);

        adminService.deleteAdmin(id);

        verify(adminRepository).deleteById(id);
    }

    @Test
    void deleteAdmin_ShouldThrowException_WhenIdNotFound() {

        when(adminRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminService.deleteAdmin(1L)
        );

        verify(adminRepository, never())
                .deleteById(anyLong());
    }
}