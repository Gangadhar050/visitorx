package com.visitor_x.controller;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.enums.PurposeOfVisit;
import com.visitor_x.service.VisitorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/visitor")
@RequiredArgsConstructor
public class VisitorController {

    private final VisitorService visitorService;

//    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public ResponseEntity<VisitorResponseDTO> registerVisitorWithPhoto(
//            @Valid @ModelAttribute VisitorRequestDTO request) {
//        return ResponseEntity.ok(visitorService.registerVisitorWithPhoto(request));
//    }

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<VisitorResponseDTO> registerVisitorWithPhoto(
            @Valid @RequestBody VisitorRequestDTO request) {
        return ResponseEntity.ok(visitorService.registerVisitorWithPhoto(request));
    }

}