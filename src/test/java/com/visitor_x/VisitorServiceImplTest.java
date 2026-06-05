package com.visitor_x;

import com.visitor_x.Enum.PurposeOfVisit;
import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.serviceImpl.VisitorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VisitorServiceImplTest {

    @Mock
    private VisitorRepository visitorRepository;

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
        requestDTO.setPurposeOfVisit(PurposeOfVisit.valueOf("INTERVIEW"));
        requestDTO.setPhotoUrl("photo.jpg");

        visitor = Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .email("gangadhar@gmail.com")
                .mobileNumber("9876543210")
                .address("Bangalore")
                .purposeOfVisit(PurposeOfVisit.valueOf("INTERVIEW"))
                .photoUrl("photo.jpg")
                .visitDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void registerVisitor_Success() {

        when(visitorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(requestDTO.getMobileNumber()))
                .thenReturn(Optional.empty());

        when(visitorRepository.save(any(Visitor.class)))
                .thenReturn(visitor);

        VisitorResponseDTO response =
                visitorService.registerVisitor(requestDTO);

        assertNotNull(response);
        assertEquals(1L, response.getVisitorId());
        assertEquals("Gangadhar", response.getName());
        assertEquals("gangadhar@gmail.com", response.getEmail());
        assertEquals("9876543210", response.getMobileNumber());

        verify(visitorRepository).findByEmail(requestDTO.getEmail());
        verify(visitorRepository).findByMobileNumber(requestDTO.getMobileNumber());
        verify(visitorRepository).save(any(Visitor.class));
    }

    @Test
    void registerVisitor_WhenEmailAlreadyExists_ShouldThrowException() {

        when(visitorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.of(visitor));

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> visitorService.registerVisitor(requestDTO)
                );

        assertEquals(
                "Email already registered",
                exception.getMessage()
        );

        verify(visitorRepository).findByEmail(requestDTO.getEmail());
        verify(visitorRepository, never()).save(any());
    }

    @Test
    void registerVisitor_WhenMobileAlreadyExists_ShouldThrowException() {

        when(visitorRepository.findByEmail(requestDTO.getEmail()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(requestDTO.getMobileNumber()))
                .thenReturn(Optional.of(visitor));

        DuplicateResourceException exception =
                assertThrows(
                        DuplicateResourceException.class,
                        () -> visitorService.registerVisitor(requestDTO)
                );

        assertEquals(
                "Mobile number already registered",
                exception.getMessage()
        );

        verify(visitorRepository).findByEmail(requestDTO.getEmail());
        verify(visitorRepository).findByMobileNumber(requestDTO.getMobileNumber());
        verify(visitorRepository, never()).save(any());
    }

    @Test
    void registerVisitor_VerifySaveCalledOnce() {

        when(visitorRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(anyString()))
                .thenReturn(Optional.empty());

        when(visitorRepository.save(any(Visitor.class)))
                .thenReturn(visitor);

        visitorService.registerVisitor(requestDTO);

        verify(visitorRepository, times(1))
                .save(any(Visitor.class));
    }
}