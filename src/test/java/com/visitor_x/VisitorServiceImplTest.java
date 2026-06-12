package com.visitor_x.serviceImpl;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.ExportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorServiceImplTest {

    @Mock
    private VisitorRepository visitorRepository;

    @Mock
    private ExportService exportService;

    @InjectMocks
    private VisitorServiceImpl visitorService;

    private VisitorRequestDTO requestDTO;
    private Visitor visitor;

    @BeforeEach
    void setUp() {

        requestDTO = new VisitorRequestDTO();
        requestDTO.setName("Gangadhar");
        requestDTO.setEmail("gangadhar@gmail.com");
        requestDTO.setMobileNumber("9876543210");
        requestDTO.setAddress("Bangalore");
        requestDTO.setPurposeOfVisit(com.visitor_x.enums.PurposeOfVisit.INTERVIEW);

        visitor = Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .email("gangadhar@gmail.com")
                .mobileNumber("9876543210")
                .address("Bangalore")
                .purposeOfVisit(com.visitor_x.enums.PurposeOfVisit.INTERVIEW)
                .photo("test photo data".getBytes())
                .visitDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void registerVisitor_Success() {

        when(visitorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(requestDTO.getMobileNumber()))
                .thenReturn(Optional.empty());

        when(visitorRepository.save(ArgumentMatchers.any(Visitor.class)))
                .thenReturn(visitor);

        VisitorResponseDTO response =
                visitorService.registerVisitor(requestDTO);

        assertNotNull(response);
        assertEquals("Gangadhar", response.getName());
        assertEquals("gangadhar@gmail.com", response.getEmail());

        verify(visitorRepository).save(any(Visitor.class));
        verify(exportService).autoSaveToFile();
    }

    @Test
    void registerVisitor_InvalidEmail() {

        requestDTO.setEmail("test@yahoo.com");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> visitorService.registerVisitor(requestDTO));

        assertEquals("Only Gmail addresses are allowed",
                exception.getMessage());
    }

    @Test
    void registerVisitor_NullEmail() {

        requestDTO.setEmail(null);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> visitorService.registerVisitor(requestDTO));

        assertEquals("Only Gmail addresses are allowed",
                exception.getMessage());
    }

    @Test
    void registerVisitor_InvalidMobileNumber() {

        requestDTO.setMobileNumber("12345");

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> visitorService.registerVisitor(requestDTO));

        assertEquals("Mobile number must contain exactly 10 digits",
                exception.getMessage());
    }

    @Test
    void registerVisitor_NullMobileNumber() {

        requestDTO.setMobileNumber(null);

        IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class,
                        () -> visitorService.registerVisitor(requestDTO));

        assertEquals("Mobile number must contain exactly 10 digits",
                exception.getMessage());
    }

    @Test
    void registerVisitor_DuplicateEmail() {

        when(visitorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.of(visitor));

        assertThrows(DuplicateResourceException.class,
                () -> visitorService.registerVisitor(requestDTO));

        verify(visitorRepository, never())
                .save(any(Visitor.class));
    }

    @Test
    void registerVisitor_DuplicateMobileNumber() {

        when(visitorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(
                requestDTO.getMobileNumber()))
                .thenReturn(Optional.of(visitor));

        assertThrows(DuplicateResourceException.class,
                () -> visitorService.registerVisitor(requestDTO));

        verify(visitorRepository, never())
                .save(any(Visitor.class));
    }

    @Test
    void getVisitorById_Success() {

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.of(visitor));

        VisitorResponseDTO response =
                visitorService.getVisitorById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getVisitorId());
        assertEquals("Gangadhar", response.getName());
    }

    @Test
    void getVisitorById_NotFound() {

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> visitorService.getVisitorById(1L));
    }
}