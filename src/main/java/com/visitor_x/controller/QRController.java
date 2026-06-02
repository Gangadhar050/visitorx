package com.visitor_x.controller;


import com.google.zxing.WriterException;
import com.visitor_x.service.QRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QRController {

    private final QRService qrService;

    @GetMapping(
            value = "/generate",
            produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQR()
            throws WriterException, IOException {

        String registrationUrl =
                "http://localhost:8080/api/visitors/register";

        byte[] qrCode =
                qrService.generateQRCode(
                        registrationUrl);

        return ResponseEntity.ok(qrCode);
    }
}
