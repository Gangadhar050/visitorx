package com.visitor_x.controller;

import com.visitor_x.service.ExportService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class ExportController {

    private final ExportService exportService;

    @GetMapping("/export")
    public void exportVisitors(
            HttpServletResponse response) {

        exportService.exportVisitors(
                response);
    }
}