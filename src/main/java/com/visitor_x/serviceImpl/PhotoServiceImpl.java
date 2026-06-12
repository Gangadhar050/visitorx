package com.visitor_x.serviceImpl;

import com.visitor_x.service.PhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
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

    @Override
    public byte[] convertToJpg(MultipartFile file) {
        try {
            if (!isValidImage(file)) {
                throw new IllegalArgumentException("Invalid image file. Please upload a valid image (JPG, PNG, GIF, etc.)");
            }

            if (file.getSize() > MAX_FILE_SIZE) {
                throw new IllegalArgumentException("File size exceeds maximum limit of 5MB");
            }

            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());

            if (bufferedImage == null) {
                throw new IllegalArgumentException("Unable to read image file");
            }

            BufferedImage jpgImage = new BufferedImage(
                    bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB
            );

            // Fill white background to avoid black areas for transparent PNG/GIF/WebP
            Graphics2D g = jpgImage.createGraphics();
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, jpgImage.getWidth(), jpgImage.getHeight());
            g.drawImage(bufferedImage, 0, 0, null);
            g.dispose();

            ByteArrayOutputStream jpgOutput = new ByteArrayOutputStream();
            boolean written = ImageIO.write(jpgImage, "jpg", jpgOutput);
            if (!written) {
                throw new IOException("No suitable JPG writer found");
            }

            log.info("Image successfully converted to JPG format. Size: {} bytes", jpgOutput.size());
            return jpgOutput.toByteArray();

        } catch (IOException e) {
            log.error("Error converting image to JPG format", e);
            throw new RuntimeException("Failed to process image: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isValidImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return false;
        }

        String contentType = file.getContentType();
        if (contentType == null) {
            return false;
        }

        return ALLOWED_MIME_TYPES.contains(contentType.toLowerCase());
    }
}