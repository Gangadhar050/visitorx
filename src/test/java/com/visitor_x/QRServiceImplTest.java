package com.visitor_x;


import com.google.zxing.WriterException;
import com.visitor_x.serviceImpl.QRServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class QRServiceImplTest {

    private QRServiceImpl qrService;

    @BeforeEach
    void setUp() {
        qrService = new QRServiceImpl();
    }

    @Test
    void generateQRCode_ShouldReturnByteArray()
            throws WriterException, IOException {

        String text = "Visitor-X QR Code";

        byte[] result = qrService.generateQRCode(text);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void generateQRCode_ShouldGenerateQRCodeForValidText()
            throws WriterException, IOException {

        byte[] result =
                qrService.generateQRCode("https://visitorx.com");

        assertNotNull(result);
        assertFalse(result.length == 0);
    }

    @Test
    void generateQRCode_ShouldGenerateQRCodeForEmptyString()
            throws WriterException, IOException {

        byte[] result =
                qrService.generateQRCode("");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void generateQRCode_ShouldGenerateDifferentQRCodeForDifferentText()
            throws WriterException, IOException {

        byte[] qr1 =
                qrService.generateQRCode("Visitor1");

        byte[] qr2 =
                qrService.generateQRCode("Visitor2");

        assertNotNull(qr1);
        assertNotNull(qr2);

        assertNotEquals(
                new String(qr1),
                new String(qr2)
        );
    }
}
