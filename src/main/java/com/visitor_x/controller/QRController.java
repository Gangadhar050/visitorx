package com.visitor_x.controller;

import com.visitor_x.service.QRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class QRController {

    private final QRService qrService;

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateQRCode() {

        try {

            String registrationUrl =
                    "http://localhost:8080/api/visitors/register";

            byte[] qrCode =
                    qrService.generateQRCode(
                            registrationUrl
                    );

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "inline; filename=qrcode.png"
                    )
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCode);

        } catch (Exception e) {

            return ResponseEntity.internalServerError()
                    .build();
        }
    }
}