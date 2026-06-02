package com.visitor_x.controller;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.repository.VisitorRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class VisitorController {

    private final VisitorRepository visitorRepository;
@PostMapping("/register")
    public Visitor registerVisitor(@Valid @RequestParam VisitorRequestDTO request) {
        Visitor visitor = new Visitor();
        visitor.setName(request.getName());
        visitor.setEmail(request.getEmail());
        visitor.setAddress(request.getAddress());
        visitor.setMobileNumber(request.getMobileNumber());
        visitor.setPurposeOfVisit(request.getPurposeOfVisit());
        visitor.setPhotoUrl(request.getPhotoUrl());
        return visitorRepository.save(visitor);
    }
}
