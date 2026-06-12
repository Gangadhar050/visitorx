package com.visitor_x.controller;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.service.VisitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

    @PostMapping("/register")
    public ResponseEntity<VisitorResponseDTO> registerVisitor(
            @Valid @RequestBody VisitorRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitorService
                        .registerVisitor(request));

    }

    @PostMapping("/register-with-photo")
    public ResponseEntity<VisitorResponseDTO> registerVisitorWithPhoto(
            @RequestParam("name") String name,
            @RequestParam("mobileNumber") String mobileNumber,
            @RequestParam("email") String email,
            @RequestParam(value = "purposeOfVisit", required = false) String purposeOfVisit,
            @RequestParam("photo") MultipartFile photo) {

        VisitorRequestDTO request = new VisitorRequestDTO();
        request.setName(name);
        request.setMobileNumber(mobileNumber);
        request.setEmail(email);
        if (purposeOfVisit != null && !purposeOfVisit.isEmpty()) {
            request.setPurposeOfVisit(
                    com.visitor_x.enums.PurposeOfVisit.valueOf(purposeOfVisit)
            );
        }
        request.setPhoto(photo);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(visitorService.registerVisitorWithPhoto(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VisitorResponseDTO> getVisitor(@PathVariable Long id) {
        return ResponseEntity.ok(visitorService.getVisitorById(id));
    }
}