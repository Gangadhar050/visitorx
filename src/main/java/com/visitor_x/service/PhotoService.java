package com.visitor_x.service;

import org.springframework.web.multipart.MultipartFile;

public interface PhotoService {

    /**
     * Convert uploaded image to JPG format
     * @param file The image file to convert
     * @return byte array of JPG image
     */
    byte[] convertToJpg(MultipartFile file);

    /**
     * Validate if the uploaded file is a valid image
     * @param file The file to validate
     * @return true if valid image, false otherwise
     */
    boolean isValidImage(MultipartFile file);
}

