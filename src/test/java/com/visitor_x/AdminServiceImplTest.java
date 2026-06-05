package com.visitor_x;


import com.visitor_x.dto.AdminRequestDTO;
import com.visitor_x.dto.AdminResponseDTO;
import com.visitor_x.entity.Admin;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.AdminRepository;
import com.visitor_x.serviceImpl.AdminServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminServiceImplTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AdminServiceImpl adminService;

    private Admin admin;
    private AdminRequestDTO requestDTO;

    @BeforeEach
    void setUp() {

        requestDTO = new AdminRequestDTO();
        requestDTO.setUsername("admin");
        requestDTO.setPassword("admin123");

        admin = Admin.builder()
                .adminId(1L)
                .username("admin")
                .password("encodedPassword")
                .role("ADMIN")
                .build();
    }

    @Test
    void createAdmin_Success() {

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("admin123"))
                .thenReturn("encodedPassword");

        when(adminRepository.save(any(Admin.class)))
                .thenReturn(admin);

        AdminResponseDTO response =
                adminService.createAdmin(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getAdminId());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());

        verify(adminRepository).findByUsername("admin");
        verify(passwordEncoder).encode("admin123");
        verify(adminRepository).save(any(Admin.class));
    }

    @Test
    void createAdmin_WhenUsernameExists_ShouldThrowException() {

        when(adminRepository.findByUsername("admin"))
                .thenReturn(Optional.of(admin));

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> adminService.createAdmin(requestDTO)
                );

        assertEquals(
                "Username already exists",
                exception.getMessage()
        );

        verify(adminRepository).findByUsername("admin");
        verify(adminRepository, never()).save(any());
    }

    @Test
    void getAdmin_Success() {

        when(adminRepository.findById(1L))
                .thenReturn(Optional.of(admin));

        AdminResponseDTO response =
                adminService.getAdmin(1L);

        assertNotNull(response);
        assertEquals(1L, response.getAdminId());
        assertEquals("admin", response.getUsername());
        assertEquals("ADMIN", response.getRole());
    }

    @Test
    void getAdmin_NotFound_ShouldThrowException() {

        when(adminRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> adminService.getAdmin(1L)
                );

        assertEquals(
                "Admin not found with id: 1",
                exception.getMessage()
        );
    }

    @Test
    void getAllAdmins_Success() {

        Admin admin2 = Admin.builder()
                .adminId(2L)
                .username("superadmin")
                .role("ADMIN")
                .build();

        when(adminRepository.findAll())
                .thenReturn(List.of(admin, admin2));

        List<AdminResponseDTO> result =
                adminService.getAllAdmins();

        assertNotNull(result);
        assertEquals(2, result.size());

        assertEquals("admin", result.get(0).getUsername());
        assertEquals("superadmin", result.get(1).getUsername());
    }

    @Test
    void getAllAdmins_EmptyList() {

        when(adminRepository.findAll())
                .thenReturn(List.of());

        List<AdminResponseDTO> result =
                adminService.getAllAdmins();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void deleteAdmin_Success() {

        when(adminRepository.existsById(1L))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                adminService.deleteAdmin(1L));

        verify(adminRepository).deleteById(1L);
    }

    @Test
    void deleteAdmin_NotFound_ShouldThrowException() {

        when(adminRepository.existsById(1L))
                .thenReturn(false);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> adminService.deleteAdmin(1L)
                );

        assertEquals(
                "Admin not found with id: 1",
                exception.getMessage()
        );

        verify(adminRepository, never())
                .deleteById(anyLong());
    }
}
