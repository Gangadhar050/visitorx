package com.visitor_x;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.enums.PurposeOfVisit;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.serviceImpl.VisitorServiceImpl;
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

    @Test
    void registerVisitor_ShouldSaveVisitorSuccessfully() {

        VisitorRequestDTO request = new VisitorRequestDTO();
        request.setName("John");
        request.setEmail("john@gmail.com");
        request.setMobileNumber("9876543210");
        request.setAddress("Bangalore");
        request.setPurposeOfVisit(PurposeOfVisit.valueOf("INTERVIEW"));
        request.setPhotoUrl("photo.jpg");

        Visitor savedVisitor = Visitor.builder()
                .visitorId(1L)
                .name("John")
                .email("john@gmail.com")
                .mobileNumber("9876543210")
                .address("Bangalore")
                .purposeOfVisit(PurposeOfVisit.valueOf("INTERVIEW"))
                .photoUrl("photo.jpg")
                .visitDateTime(LocalDateTime.now())
                .build();

        when(visitorRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber("9876543210"))
                .thenReturn(Optional.empty());

        when(visitorRepository.save(any(Visitor.class)))
                .thenReturn(savedVisitor);

        VisitorResponseDTO response =
                visitorService.registerVisitor(request);

        assertNotNull(response);
        assertEquals("John", response.getName());
        assertEquals("john@gmail.com", response.getEmail());
        assertEquals("9876543210", response.getMobileNumber());

        verify(visitorRepository, times(1))
                .save(any(Visitor.class));
    }

    @Test
    void registerVisitor_ShouldThrowException_WhenEmailExists() {

        VisitorRequestDTO request = new VisitorRequestDTO();
        request.setEmail("john@gmail.com");

        Visitor visitor = new Visitor();

        when(visitorRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.of(visitor));

        assertThrows(
                DuplicateResourceException.class,
                () -> visitorService.registerVisitor(request)
        );

        verify(visitorRepository, never())
                .save(any());
    }

    @Test
    void registerVisitor_ShouldThrowException_WhenMobileExists() {

        VisitorRequestDTO request = new VisitorRequestDTO();
        request.setEmail("john@gmail.com");
        request.setMobileNumber("9876543210");

        Visitor visitor = new Visitor();

        when(visitorRepository.findByEmail("john@gmail.com"))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber("9876543210"))
                .thenReturn(Optional.of(visitor));

        assertThrows(
                DuplicateResourceException.class,
                () -> visitorService.registerVisitor(request)
        );

        verify(visitorRepository, never())
                .save(any());
    }

    @Test
    void registerVisitor_ShouldMapEntityToDTOCorrectly() {

        VisitorRequestDTO request = new VisitorRequestDTO();
        request.setName("John");
        request.setEmail("john@gmail.com");
        request.setMobileNumber("9876543210");

        Visitor visitor = Visitor.builder()
                .visitorId(1L)
                .name("John")
                .email("john@gmail.com")
                .mobileNumber("9876543210")
                .build();

        when(visitorRepository.findByEmail(anyString()))
                .thenReturn(Optional.empty());

        when(visitorRepository.findByMobileNumber(anyString()))
                .thenReturn(Optional.empty());

        when(visitorRepository.save(any()))
                .thenReturn(visitor);

        VisitorResponseDTO response =
                visitorService.registerVisitor(request);

        assertEquals(1L, response.getVisitorId());
        assertEquals("John", response.getName());
        assertEquals("john@gmail.com", response.getEmail());
    }
}
