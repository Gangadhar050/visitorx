package com.visitor_x;

import com.google.zxing.WriterException;
import com.visitor_x.serviceImpl.QRServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class QRServiceImplTest {

    private QRServiceImpl qrService;

    @BeforeEach
    void setUp() {
        qrService = new QRServiceImpl();

        // Inject value of @Value("${app.qr.save-path}")
        ReflectionTestUtils.setField(
                qrService,
                "savePath",
                "target/test-qrcodes"
        );
    }

    @Test
    void generateQRCode_ShouldReturnByteArray()
            throws WriterException, IOException {

        byte[] result =
                qrService.generateQRCode("VisitorX QR Test");

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void saveQRCode_ShouldCreatePngFile()
            throws WriterException, IOException {

        String filePath =
                qrService.saveQRCode("VisitorX Save QR Test");

        assertNotNull(filePath);

        Path path = Path.of(filePath);

        assertTrue(Files.exists(path));
        assertTrue(Files.size(path) > 0);
        assertTrue(filePath.endsWith(".png"));
    }

    @Test
    void saveQRCode_ShouldCreateDirectoryIfNotExists()
            throws WriterException, IOException {

        String customDir = "target/new-qr-folder";

        ReflectionTestUtils.setField(
                qrService,
                "savePath",
                customDir
        );

        qrService.saveQRCode("Directory Creation Test");

        assertTrue(Files.exists(Path.of(customDir)));
    }


    @Test
    void saveQRCode_ShouldGenerateUniqueFileNames()
            throws Exception {

        String file1 =
                qrService.saveQRCode("QR1");

        Thread.sleep(1000);

        String file2 =
                qrService.saveQRCode("QR2");

        assertNotEquals(file1, file2);
    }
}
