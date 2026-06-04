package com.visitor_x.controller;

import com.google.zxing.WriterException;
import com.visitor_x.service.QRService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
public class QRController {

    private final QRService qrService;

    @GetMapping("/generate")
    public ResponseEntity<byte[]> generateQRCode(
            @RequestParam String url)
            throws WriterException, IOException {

        byte[] qrCode = qrService.generateQRCode(url);

        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "inline; filename=qrcode.png")
                .body(qrCode);
    }
}

//
//package com.visitor_x.controller;
//
//import com.google.zxing.WriterException;
//import com.visitor_x.service.QRService;
//import lombok.RequiredArgsConstructor;
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
//    @GetMapping("/generate")
//    public String generateQRCode(@RequestParam String url)
//            throws WriterException, IOException {
//
//        return qrService.generateQRCode(url);
//    }
//}