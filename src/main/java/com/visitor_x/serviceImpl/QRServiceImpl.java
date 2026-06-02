package com.visitor_x.serviceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.visitor_x.service.QRService;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
public class QRServiceImpl implements QRService {
    @Override
    public byte[] generateQRCode(String text) throws WriterException, IOException {

        QRCodeWriter qrCodeWriter = new QRCodeWriter();

        // Here you would generate the QR code and convert it to a byte array
        BitMatrix bitMatrix = qrCodeWriter.encode(text,
                BarcodeFormat.QR_CODE, 300, 300);


        ByteArrayOutputStream outputStream =
                new ByteArrayOutputStream();

        MatrixToImageWriter.writeToStream(
                bitMatrix,
                "PNG",
                outputStream);

        return outputStream.toByteArray();

    }
    }
