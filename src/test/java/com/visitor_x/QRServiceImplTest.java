package com.visitor_x;

import com.google.zxing.WriterException;
import com.visitor_x.serviceImpl.QRServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class QRServiceImplTest {

    private QRServiceImpl qrService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        qrService = new QRServiceImpl();

        ReflectionTestUtils.setField(
                qrService,
                "savePath",
                tempDir.toString()
        );
    }

    @Test
    void generateQRCode_Success() throws WriterException, IOException {

        byte[] qrBytes =
                qrService.generateQRCode("https://visitorx.com");

        assertNotNull(qrBytes);
        assertTrue(qrBytes.length > 0);
    }

    @Test
    void generateQRCode_NullText() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> qrService.generateQRCode(null)
                );

        assertEquals(
                "QR text cannot be empty",
                exception.getMessage()
        );
    }

    @Test
    void generateQRCode_EmptyText() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> qrService.generateQRCode("")
                );

        assertEquals(
                "QR text cannot be empty",
                exception.getMessage()
        );
    }

    @Test
    void saveQRCode_Success()
            throws WriterException, IOException {

        String filePath =
                qrService.saveQRCode("VisitorX QR");

        assertNotNull(filePath);

        Path savedFile = Path.of(filePath);

        assertTrue(Files.exists(savedFile));
        assertTrue(filePath.endsWith(".png"));
    }

    @Test
    void saveQRCode_NullText() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> qrService.saveQRCode(null)
                );

        assertEquals(
                "QR text cannot be empty",
                exception.getMessage()
        );
    }

    @Test
    void saveQRCode_EmptyText() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> qrService.saveQRCode(" ")
                );

        assertEquals(
                "QR text cannot be empty",
                exception.getMessage()
        );
    }
}