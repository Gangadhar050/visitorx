package com.visitor_x.controller;

import com.google.zxing.WriterException;
import com.visitor_x.service.QRService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QRController {

    private final QRService qrService;

    @Value("${app.visitor.form-url}")
    private String visitorFormUrl;

    // Returns QR as PNG image in response
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/generate",
            produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQR()
            throws WriterException, IOException {
        return ResponseEntity.ok(
                qrService.generateQRCode(visitorFormUrl));
    }
//new
    @GetMapping("/generate-form")
    public ResponseEntity<String> generateFormQr() throws Exception {
        String path = qrService.saveVisitorFormQRCode();
        return ResponseEntity.ok(path);
    }

    // Saves QR to folder and returns file path
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/save")
    public ResponseEntity<Map<String, String>> saveQR()
            throws WriterException, IOException {
        String path = qrService.saveQRCode(visitorFormUrl);
        return ResponseEntity.ok(
                Map.of("savedAt", path,
                        "message", "QR code saved successfully"));
    }
}