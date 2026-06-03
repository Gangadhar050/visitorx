
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

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QRController {

    private final QRService qrService;

    @Value("${app.visitor.form-url}")
    private String visitorFormUrl;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping(value = "/generate",
            produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateQR()
            throws WriterException, IOException {
        return ResponseEntity.ok(
                qrService.generateQRCode(visitorFormUrl));
    }
}
//package com.visitor_x.controller;
//
//import com.google.zxing.WriterException;
//import com.visitor_x.service.QRService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.io.IOException;
//
//@RestController
//@RequestMapping("/api/qr")
//@RequiredArgsConstructor
//public class QRController {
//
//    private final QRService qrService;
//
//    @Value("${app.visitor.form-url}")
//    private String visitorFormUrl; // frontend form URL from config
//
//    // Only ADMIN with valid JWT can generate QR
//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping(value = "/generate", produces = MediaType.IMAGE_PNG_VALUE)
//    public ResponseEntity<byte[]> generateQR()
//            throws WriterException, IOException {
//
//        byte[] qrCode = qrService.generateQRCode(visitorFormUrl);
//        return ResponseEntity.ok(qrCode);
//    }
//}

