package com.visitor_x.controller;

import com.visitor_x.dto.AdminRequestDTO;
import com.visitor_x.dto.AdminResponseDTO;
import com.visitor_x.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")  // all endpoints require ADMIN JWT
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/create")
    public ResponseEntity<AdminResponseDTO> createAdmin(
            @Valid @RequestBody AdminRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(adminService.createAdmin(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDTO> getAdmin(
            @PathVariable Long id) {
        return ResponseEntity.ok(adminService.getAdmin(id));
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminResponseDTO>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdmin(
            @PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.noContent().build();
    }
}