package com.visitor_x.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@Service
public class QRService {

    public byte[] generateQRCode(String text) throws Exception {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        BitMatrix bitMatrix = qrCodeWriter.encode(
                text,
                BarcodeFormat.QR_CODE,
                300,
                300
        );

        BufferedImage image = new BufferedImage(
                300,
                300,
                BufferedImage.TYPE_INT_RGB
        );

        for (int x = 0; x < 300; x++) {
            for (int y = 0; y < 300; y++) {

                image.setRGB(
                        x,
                        y,
                        bitMatrix.get(x, y)
                                ? 0x000000
                                : 0xFFFFFF
                );
            }
        }

        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        ImageIO.write(image, "PNG", outputStream);

        return outputStream.toByteArray();
    }
}