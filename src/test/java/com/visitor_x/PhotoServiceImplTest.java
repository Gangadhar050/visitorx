package com.visitor_x;

import com.visitor_x.serviceImpl.PhotoServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class PhotoServiceImplTest {

    private PhotoServiceImpl photoService;

    @BeforeEach
    void setUp() {
        photoService = new PhotoServiceImpl();
    }

    @Test
    void convertBase64ToJpg_ValidImage() throws Exception {

        BufferedImage image =
                new BufferedImage(100, 100,
                        BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        ImageIO.write(image, "png", outputStream);

        String base64 =
                Base64.getEncoder()
                        .encodeToString(outputStream.toByteArray());

        byte[] result =
                photoService.convertBase64ToJpg(base64);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void convertBase64ToJpg_WithDataUriPrefix() throws Exception {

        BufferedImage image =
                new BufferedImage(100, 100,
                        BufferedImage.TYPE_INT_RGB);

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        ImageIO.write(image, "png", outputStream);

        String base64 =
                "data:image/png;base64," +
                        Base64.getEncoder()
                                .encodeToString(
                                        outputStream.toByteArray());

        byte[] result =
                photoService.convertBase64ToJpg(base64);

        assertNotNull(result);
        assertTrue(result.length > 0);
    }

    @Test
    void convertBase64ToJpg_InvalidBase64() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> photoService.convertBase64ToJpg(
                                "invalid-base64-data")
                );

        assertEquals(
                "Invalid Base64 image data",
                exception.getMessage());
    }

    @Test
    void convertBase64ToJpg_EmptyString() {

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> photoService.convertBase64ToJpg("")
                );

        assertEquals(
                "Invalid Base64 image data",
                exception.getMessage());
    }

    @Test
    void isValidImage_Jpeg() {

        MultipartFile file =
                new MockMultipartFile(
                        "photo",
                        "test.jpg",
                        "image/jpeg",
                        "content".getBytes());

        assertTrue(photoService.isValidImage(file));
    }

    @Test
    void isValidImage_Png() {

        MultipartFile file =
                new MockMultipartFile(
                        "photo",
                        "test.png",
                        "image/png",
                        "content".getBytes());

        assertTrue(photoService.isValidImage(file));
    }

    @Test
    void isValidImage_InvalidMimeType() {

        MultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.pdf",
                        "application/pdf",
                        "content".getBytes());

        assertFalse(photoService.isValidImage(file));
    }

    @Test
    void isValidImage_NullContentType() {

        MultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test",
                        null,
                        "content".getBytes());

        assertFalse(photoService.isValidImage(file));
    }

    @Test
    void isValidImage_EmptyFile() {

        MultipartFile file =
                new MockMultipartFile(
                        "file",
                        "test.jpg",
                        "image/jpeg",
                        new byte[0]);

        assertFalse(photoService.isValidImage(file));
    }

    @Test
    void isValidImage_NullFile() {

        assertFalse(photoService.isValidImage(null));
    }
}
