package com.visitor_x.serviceImpl;

import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminDashboardServiceImplTest {

    @Mock
    private VisitorRepository visitorRepository;

    @InjectMocks
    private AdminDashboardServiceImpl adminDashboardService;

    @Test
    void getDashboard_Success() {

        when(visitorRepository.count()).thenReturn(100L);

        when(visitorRepository.countByVisitDateTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(10L, 40L, 80L);

        DashboardResponse response =
                adminDashboardService.getDashboard();

        assertNotNull(response);
        assertEquals(100L, response.getTotalVisitors());
        assertEquals(10L, response.getTodayVisitors());
        assertEquals(40L, response.getThisWeekVisitors());
        assertEquals(80L, response.getThisMonthVisitors());
    }

    @Test
    void getAllVisitors_Success() {

        Visitor visitor = createVisitor();

        Page<Visitor> page =
                new PageImpl<>(List.of(visitor));

        Pageable pageable =
                PageRequest.of(0, 10);

        when(visitorRepository.existsById(1L))
                .thenReturn(true);

        when(visitorRepository.findAll(pageable))
                .thenReturn(page);

        Page<VisitorResponseDTO> result =
                adminDashboardService.getAllVisitors(pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getAllVisitors_NoVisitorsFound() {

        Pageable pageable =
                PageRequest.of(0, 10);

        when(visitorRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminDashboardService.getAllVisitors(pageable)
        );
    }

    @Test
    void getVisitor_Success() {

        Visitor visitor = createVisitor();

        when(visitorRepository.existsById(1L))
                .thenReturn(true);

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.of(visitor));

        VisitorResponseDTO response =
                adminDashboardService.getVisitor(1L);

        assertEquals(1L, response.getVisitorId());
        assertEquals("Gangadhar", response.getName());
    }

    @Test
    void getVisitor_InvalidId() {

        assertThrows(
                IllegalArgumentException.class,
                () -> adminDashboardService.getVisitor(0L)
        );
    }

    @Test
    void getVisitor_NotFound() {

        when(visitorRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminDashboardService.getVisitor(1L)
        );
    }

    @Test
    void searchVisitors_Success() {

        Visitor visitor = createVisitor();

        Page<Visitor> page =
                new PageImpl<>(List.of(visitor));

        Pageable pageable =
                PageRequest.of(0, 10);

        when(visitorRepository.existsById(1L))
                .thenReturn(true);

        when(visitorRepository.searchByNameOrMobile(
                eq("Gangadhar"),
                eq(pageable)))
                .thenReturn(page);

        Page<VisitorResponseDTO> result =
                adminDashboardService.searchVisitors(
                        "Gangadhar",
                        pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void searchVisitors_EmptyKeyword() {

        Pageable pageable =
                PageRequest.of(0, 10);

        assertThrows(
                IllegalArgumentException.class,
                () -> adminDashboardService.searchVisitors(
                        "",
                        pageable)
        );
    }

    @Test
    void searchVisitors_NoVisitorsFound() {

        Pageable pageable =
                PageRequest.of(0, 10);

        when(visitorRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminDashboardService.searchVisitors(
                        "Gangadhar",
                        pageable)
        );
    }

    @Test
    void getTodayVisitors_Success() {

        Visitor visitor = createVisitor();

        when(visitorRepository.existsById(1L))
                .thenReturn(true);

        when(visitorRepository.findByVisitDateTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(visitor));

        List<VisitorResponseDTO> result =
                adminDashboardService.getTodayVisitors();

        assertEquals(1, result.size());
    }

    @Test
    void getTodayVisitors_NoVisitors() {

        when(visitorRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminDashboardService.getTodayVisitors()
        );
    }

    @Test
    void deleteVisitor_Success() {

        when(visitorRepository.existsById(1L))
                .thenReturn(true);

        adminDashboardService.deleteVisitor(1L);

        verify(visitorRepository)
                .deleteById(1L);
    }

    @Test
    void deleteVisitor_NotFound() {

        when(visitorRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> adminDashboardService.deleteVisitor(1L)
        );
    }

    private Visitor createVisitor() {

        return Visitor.builder()
                .visitorId(1L)
                .name("Gangadhar")
                .email("gangadhar@gmail.com")
                .mobileNumber("9876543210")
                .address("Bangalore")
                .purposeOfVisit("Meeting")
                .photoUrl("photo.jpg")
                .visitDateTime(LocalDateTime.now())
                .build();
    }
}