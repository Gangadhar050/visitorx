package com.visitor_x.serviceImpl;


import com.visitor_x.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceImpl implements PhotoService {

    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(
            Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp")
    );

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    // Fixed thumbnail size so the photo fits inside one Excel cell
    private static final int THUMB_WIDTH = 100;
    private static final int THUMB_HEIGHT = 100;

    @Override
    public byte[] convertBase64ToJpg(String photoBase64) {
        try {

            log.info("Received Base64 length: {}", photoBase64.length());

            String base64Data = photoBase64;

            if (photoBase64.contains(",")) {
                base64Data = photoBase64.substring(photoBase64.indexOf(",") + 1);
            }

            log.info("Pure Base64 length: {}", base64Data.length());

            byte[] imageBytes = Base64.getDecoder().decode(base64Data);

            log.info("Decoded bytes length: {}", imageBytes.length);

            BufferedImage bufferedImage =
                    ImageIO.read(new ByteArrayInputStream(imageBytes));

            log.info("BufferedImage: {}", bufferedImage);

            if (bufferedImage == null) {
                throw new IllegalArgumentException("Invalid image data");
            }

            // Resize to a fixed thumbnail so it fits neatly in an Excel cell
            BufferedImage jpgImage = new BufferedImage(
                    THUMB_WIDTH,
                    THUMB_HEIGHT,
                    BufferedImage.TYPE_INT_RGB
            );

            Graphics2D g = jpgImage.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, THUMB_WIDTH, THUMB_HEIGHT);
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                    RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.drawImage(bufferedImage, 0, 0, THUMB_WIDTH, THUMB_HEIGHT, null);
            g.dispose();

            ByteArrayOutputStream jpgOutput = new ByteArrayOutputStream();
            boolean written = ImageIO.write(jpgImage, "jpg", jpgOutput);
            if (!written) {
                throw new IOException("No suitable JPG writer found");
            }

            return jpgOutput.toByteArray();

        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Base64 image data");
        } catch (IOException e) {
            throw new RuntimeException("Failed to process image: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) return false;
        String contentType = file.getContentType();
        if (contentType == null) return false;
        return ALLOWED_MIME_TYPES.contains(contentType.toLowerCase());
    }
}

//package com.visitor_x.serviceImpl;
//
//
//import com.visitor_x.service.PhotoService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import javax.imageio.ImageIO;
//import java.awt.*;
//import java.awt.image.BufferedImage;
//import java.io.ByteArrayInputStream;
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.util.Arrays;
//import java.util.Base64;
//import java.util.HashSet;
//import java.util.Set;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class PhotoServiceImpl implements PhotoService {
//
//    private static final Set<String> ALLOWED_MIME_TYPES = new HashSet<>(
//            Arrays.asList("image/jpeg", "image/jpg", "image/png", "image/gif", "image/webp", "image/bmp")
//    );
//
//    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB
//
//    @Override
//    public byte[] convertBase64ToJpg(String photoBase64) {
//        try {
//
//            log.info("Received Base64 length: {}", photoBase64.length());
//
//            String base64Data = photoBase64;
//
//            if (photoBase64.contains(",")) {
//                base64Data = photoBase64.substring(photoBase64.indexOf(",") + 1);
//            }
//
//            log.info("Pure Base64 length: {}", base64Data.length());
//
//            byte[] imageBytes = Base64.getDecoder().decode(base64Data);
//
//            log.info("Decoded bytes length: {}", imageBytes.length);
//
//            BufferedImage bufferedImage =
//                    ImageIO.read(new ByteArrayInputStream(imageBytes));
//
//            log.info("BufferedImage: {}", bufferedImage);
//
//            if (bufferedImage == null) {
//                throw new IllegalArgumentException("Invalid image data");
//            }
//
//            // existing code...
//
//            BufferedImage jpgImage = new BufferedImage(
//                    bufferedImage.getWidth(),
//                    bufferedImage.getHeight(),
//                    BufferedImage.TYPE_INT_RGB
//            );
//
//            Graphics2D g = jpgImage.createGraphics();
//            g.setColor(Color.WHITE);
//            g.fillRect(0, 0, jpgImage.getWidth(), jpgImage.getHeight());
//            g.drawImage(bufferedImage, 0, 0, null);
//            g.dispose();
//
//            ByteArrayOutputStream jpgOutput = new ByteArrayOutputStream();
//            boolean written = ImageIO.write(jpgImage, "jpg", jpgOutput);
//            if (!written) {
//                throw new IOException("No suitable JPG writer found");
//            }
//
//            return jpgOutput.toByteArray();
//
//        } catch (IllegalArgumentException e) {
//            throw new IllegalArgumentException("Invalid Base64 image data");
//        } catch (IOException e) {
//            throw new RuntimeException("Failed to process image: " + e.getMessage(), e);
//        }
//    }
//
//    @Override
//    public boolean isValidImage(MultipartFile file) {
//        if (file == null || file.isEmpty()) return false;
//        String contentType = file.getContentType();
//        if (contentType == null) return false;
//        return ALLOWED_MIME_TYPES.contains(contentType.toLowerCase());
//    }
//}