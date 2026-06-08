package com.visitor_x;


import com.visitor_x.enums.PurposeOfVisit;
import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.serviceImpl.AdminDashboardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceImplTest {

    @Mock
    private VisitorRepository visitorRepository;

    @InjectMocks
    private AdminDashboardServiceImpl dashboardService;

    private Visitor visitor;

    @BeforeEach
    void setUp() {

        visitor = Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .email("gangadhar@gmail.com")
                .mobileNumber("9876543210")
                .address("Bangalore")
                .purposeOfVisit(String.valueOf(PurposeOfVisit.INTERVIEW))
                .photoUrl("photo.jpg")
                .visitDateTime(LocalDateTime.now())
                .build();
    }

    @Test
    void getDashboard_Success() {

        when(visitorRepository.count()).thenReturn(100L);

        when(visitorRepository.countByVisitDateTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(10L)
                .thenReturn(50L)
                .thenReturn(80L);

        DashboardResponse response =
                dashboardService.getDashboard();

        assertNotNull(response);
        assertEquals(100L, response.getTotalVisitors());
        assertEquals(10L, response.getTodayVisitors());
        assertEquals(50L, response.getThisWeekVisitors());
        assertEquals(80L, response.getThisMonthVisitors());
    }

    @Test
    void getAllVisitors_Success() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Visitor> visitorPage =
                new PageImpl<>(List.of(visitor));

        when(visitorRepository.findAll(pageable))
                .thenReturn(visitorPage);

        Page<VisitorResponseDTO> result =
                dashboardService.getAllVisitors(pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Gangadhar",
                result.getContent().get(0).getName());
    }

    @Test
    void getVisitor_Success() {

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.of(visitor));

        VisitorResponseDTO response =
                dashboardService.getVisitor(1L);

        assertNotNull(response);
        assertEquals(1L, response.getVisitorId());
        assertEquals("Gangadhar", response.getName());
    }

    @Test
    void getVisitor_NotFound() {

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> dashboardService.getVisitor(1L)
                );

        assertEquals(
                "Visitor not found with id: 1",
                exception.getMessage()
        );
    }

    @Test
    void searchVisitors_Success() {

        Pageable pageable = PageRequest.of(0, 10);

        Page<Visitor> visitorPage =
                new PageImpl<>(List.of(visitor));

        when(visitorRepository.searchByNameOrMobile(
                "Gangadhar",
                pageable))
                .thenReturn(visitorPage);

        Page<VisitorResponseDTO> result =
                dashboardService.searchVisitors(
                        "Gangadhar",
                        pageable);

        assertEquals(1, result.getContent().size());
        assertEquals("Gangadhar",
                result.getContent().get(0).getName());
    }

    @Test
    void getTodayVisitors_Success() {

        when(visitorRepository.findByVisitDateTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(visitor));

        List<VisitorResponseDTO> result =
                dashboardService.getTodayVisitors();

        assertEquals(1, result.size());
        assertEquals("Gangadhar",
                result.get(0).getName());
    }

    @Test
    void getTodayVisitors_EmptyList() {

        when(visitorRepository.findByVisitDateTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<VisitorResponseDTO> result =
                dashboardService.getTodayVisitors();

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteVisitor_Success() {

        when(visitorRepository.existsById(1L))
                .thenReturn(true);

        assertDoesNotThrow(() ->
                dashboardService.deleteVisitor(1L));

        verify(visitorRepository)
                .deleteById(1L);
    }

    @Test
    void deleteVisitor_NotFound() {

        when(visitorRepository.existsById(1L))
                .thenReturn(false);

        ResourceNotFoundException exception =
                assertThrows(
                        ResourceNotFoundException.class,
                        () -> dashboardService.deleteVisitor(1L)
                );

        assertEquals(
                "Visitor not found with id: 1",
                exception.getMessage()
        );

        verify(visitorRepository, never())
                .deleteById(anyLong());
    }
}
