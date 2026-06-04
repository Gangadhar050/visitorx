package com.visitor_x;

import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.serviceImpl.AdminDashboardServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
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

    @Test
    void getDashboard_ShouldReturnDashboardStatistics() {

        when(visitorRepository.count()).thenReturn(100L);
        when(visitorRepository.countByVisitDateTimeBetween(any(), any()))
                .thenReturn(10L)
                .thenReturn(30L)
                .thenReturn(60L);

        DashboardResponse response =
                dashboardService.getDashboard();

        assertNotNull(response);
        assertEquals(100L, response.getTotalVisitors());
        assertEquals(10L, response.getTodayVisitors());
        assertEquals(30L, response.getThisWeekVisitors());
        assertEquals(60L, response.getThisMonthVisitors());
    }

    @Test
    void getAllVisitors_ShouldReturnPagedVisitors() {

        Visitor visitor = Visitor.builder()
                .visitorId(1L)
                .name("John")
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Visitor> page =
                new PageImpl<>(List.of(visitor));

        when(visitorRepository.findAll(pageable))
                .thenReturn(page);

        Page<VisitorResponseDTO> result =
                dashboardService.getAllVisitors(pageable);

        assertEquals(1, result.getContent().size());
    }

    @Test
    void getVisitor_ShouldReturnVisitor_WhenIdExists() {

        Visitor visitor = Visitor.builder()
                .visitorId(1L)
                .name("John")
                .build();

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.of(visitor));

        VisitorResponseDTO response =
                dashboardService.getVisitor(1L);

        assertEquals("John", response.getName());
    }

    @Test
    void getVisitor_ShouldThrowException_WhenNotFound() {

        when(visitorRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> dashboardService.getVisitor(1L)
        );
    }

    @Test
    void searchVisitors_ShouldReturnMatchingVisitors() {

        Visitor visitor = Visitor.builder()
                .visitorId(1L)
                .name("John")
                .build();

        Pageable pageable = PageRequest.of(0, 10);

        Page<Visitor> page =
                new PageImpl<>(List.of(visitor));

        when(visitorRepository
                .searchByNameOrMobile("John", pageable))
                .thenReturn(page);

        Page<VisitorResponseDTO> result =
                dashboardService.searchVisitors(
                        "John",
                        pageable);

        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getTodayVisitors_ShouldReturnTodayVisitors() {

        Visitor visitor = Visitor.builder()
                .visitorId(1L)
                .name("John")
                .visitDateTime(LocalDateTime.now())
                .build();

        when(visitorRepository.findByVisitDateTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of(visitor));

        List<VisitorResponseDTO> result =
                dashboardService.getTodayVisitors();

        assertEquals(1, result.size());
    }

    @Test
    void getTodayVisitors_ShouldReturnEmptyList() {

        when(visitorRepository.findByVisitDateTimeBetween(
                any(LocalDateTime.class),
                any(LocalDateTime.class)))
                .thenReturn(List.of());

        List<VisitorResponseDTO> result =
                dashboardService.getTodayVisitors();

        assertTrue(result.isEmpty());
    }

    @Test
    void deleteVisitor_ShouldDeleteSuccessfully() {

        when(visitorRepository.existsById(1L))
                .thenReturn(true);

        dashboardService.deleteVisitor(1L);

        verify(visitorRepository)
                .deleteById(1L);
    }

    @Test
    void deleteVisitor_ShouldThrowException_WhenNotFound() {

        when(visitorRepository.existsById(1L))
                .thenReturn(false);

        assertThrows(
                ResourceNotFoundException.class,
                () -> dashboardService.deleteVisitor(1L)
        );

        verify(visitorRepository, never())
                .deleteById(anyLong());
    }
}
