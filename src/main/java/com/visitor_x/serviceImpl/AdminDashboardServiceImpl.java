package com.visitor_x.service.impl;

import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl
        implements AdminDashboardService {

    private final VisitorRepository visitorRepository;

    @Override
    public DashboardResponse getDashboard() {

        long totalVisitors =
                visitorRepository.count();

        LocalDate today = LocalDate.now();

        long todayVisitors =
                visitorRepository.countByVisitDateTimeBetween(
                        today.atStartOfDay(),
                        today.plusDays(1).atStartOfDay());

        return DashboardResponse.builder()
                .totalVisitors(totalVisitors)
                .todayVisitors(todayVisitors)
                .build();
    }

    @Override
    public List<Visitor> getAllVisitors() {
        return visitorRepository.findAll();
    }

    @Override
    public List<Visitor> searchVisitors(
            String keyword) {

        return visitorRepository
                .findByNameContainingIgnoreCase(
                        keyword);
    }

    @Override
    public Visitor getVisitor(Long id) {

        return visitorRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Visitor not found"));
    }

    @Override
    public void deleteVisitor(Long id) {

        Visitor visitor =
                visitorRepository.findById(id)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Visitor not found"));

        visitorRepository.delete(visitor);
    }
}