package com.visitor_x.controller;

import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/photos")
@RequiredArgsConstructor
public class PhotoController {

    private final VisitorRepository visitorRepository;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> getPhoto(@PathVariable Long id) {
        Visitor visitor = visitorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor not found with id: " + id));

        byte[] photo = visitor.getPhoto();
        if (photo == null || photo.length == 0) {
            throw new ResourceNotFoundException("No photo found for visitor id: " + id);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=visitor-" + id + ".jpg")
                .body(photo);
    }
}