package com.visitor_x.controller;

import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.entity.Visitor;
import com.visitor_x.service.AdminDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final AdminDashboardService dashboardService;

    @GetMapping("/dashboard")
    public DashboardResponse dashboard() {

        return dashboardService.getDashboard();
    }

    @GetMapping("/visitors")
    public List<Visitor> getAllVisitors() {

        return dashboardService.getAllVisitors();
    }

    @GetMapping("/visitors/{id}")
    public Visitor getVisitor(
            @PathVariable Long id) {

        return dashboardService.getVisitor(id);
    }

    @GetMapping("/visitors/search")
    public List<Visitor> searchVisitor(
            @RequestParam String keyword) {

        return dashboardService.searchVisitors(
                keyword);
    }

    @DeleteMapping("/visitors/{id}")
    public String deleteVisitor(
            @PathVariable Long id) {

        dashboardService.deleteVisitor(id);

        return "Visitor deleted successfully";
    }
}