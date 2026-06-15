package com.visitor_x.serviceImpl;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.enums.PurposeOfVisit;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.ExportService;
import com.visitor_x.service.PhotoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private PhotoService photoService;

    @InjectMocks
    private VisitorServiceImpl visitorService;

    private VisitorRequestDTO requestDTO;
    private Visitor visitor;

    @BeforeEach
    void setUp() {

        requestDTO = new VisitorRequestDTO();
        requestDTO.setName("John Doe");
        requestDTO.setEmail("john@gmail.com");
        requestDTO.setMobileNumber("9876543210");
        requestDTO.setPurposeOfVisit(PurposeOfVisit.INTERVIEW);
        requestDTO.setPhotoBase64("base64Photo");

        visitor = Visitor.builder()
                .visitorId(1L)
                .name("John Doe")
                .email("john@gmail.com")
                .mobileNumber("9876543210")
                .purposeOfVisit(PurposeOfVisit.INTERVIEW)
                .photo("photo".getBytes())
                .visitDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void registerVisitorWithPhoto_Success() {

        byte[] photoBytes = "photo".getBytes();

        when(visitorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(requestDTO.getMobileNumber()))
                .thenReturn(Optional.empty());

        when(photoService.convertBase64ToJpg(requestDTO.getPhotoBase64()))
                .thenReturn(photoBytes);

        when(visitorRepository.save(any(Visitor.class)))
                .thenReturn(visitor);

        VisitorResponseDTO response =
                visitorService.registerVisitorWithPhoto(requestDTO);

        assertNotNull(response);
        assertEquals("John Doe", response.getName());
        assertEquals("john@gmail.com", response.getEmail());

        verify(visitorRepository).save(any(Visitor.class));
        verify(exportService).autoSaveToFile();
    }

    @Test
    void registerVisitorWithPhoto_InvalidEmail() {

        requestDTO.setEmail("john@yahoo.com");

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> visitorService.registerVisitorWithPhoto(requestDTO)
                );

        assertEquals(
                "Only Gmail addresses are allowed",
                exception.getMessage()
        );
    }

    @Test
    void registerVisitorWithPhoto_InvalidMobile() {

        requestDTO.setMobileNumber("12345");

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> visitorService.registerVisitorWithPhoto(requestDTO)
                );

        assertEquals(
                "Mobile number must contain exactly 10 digits",
                exception.getMessage()
        );
    }

    @Test
    void registerVisitorWithPhoto_PhotoMissing() {

        requestDTO.setPhotoBase64("");

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> visitorService.registerVisitorWithPhoto(requestDTO)
                );

        assertEquals(
                "Photo is required",
                exception.getMessage()
        );
    }

    @Test
    void registerVisitorWithPhoto_DuplicateEmail() {

        when(visitorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.of(visitor));

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> visitorService.registerVisitorWithPhoto(requestDTO)
                );

        assertEquals(
                "Email already registered",
                exception.getMessage()
        );
    }

    @Test
    void registerVisitorWithPhoto_DuplicateMobile() {

        when(visitorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(requestDTO.getMobileNumber()))
                .thenReturn(Optional.of(visitor));

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> visitorService.registerVisitorWithPhoto(requestDTO)
                );

        assertEquals(
                "Mobile number already registered",
                exception.getMessage()
        );
    }

    @Test
    void getVisitorById_Success() {

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.of(visitor));

        VisitorResponseDTO response =
                visitorService.getVisitorById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getVisitorId());
        assertEquals("John Doe", response.getName());
    }

    @Test
    void getVisitorById_NotFound() {

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> visitorService.getVisitorById(1L)
                );

        assertEquals(
                "Visitor not found with id: 1",
                exception.getMessage()
        );
    }
}