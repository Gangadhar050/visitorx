package com.visitor_x.controller;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.service.VisitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visitors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VisitorController {

    private final VisitorService visitorService; // ← use service, not repository

    @PostMapping("/register")
    public ResponseEntity<VisitorResponseDTO> registerVisitor(
            @Valid @RequestBody VisitorRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitorService
                        .registerVisitor(request));

    }
}