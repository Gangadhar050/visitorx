package com.visitor_x.serviceImpl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
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
    public byte[] generateQRCode(String text)
            throws WriterException, IOException {

        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix bitMatrix = writer.encode(
                text, BarcodeFormat.QR_CODE, 300, 300);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
        return outputStream.toByteArray();
    }
}


//package com.visitor_x.serviceImpl;
//
//import com.google.zxing.BarcodeFormat;
//import com.google.zxing.WriterException;
//import com.google.zxing.client.j2se.MatrixToImageWriter;
//import com.google.zxing.common.BitMatrix;
//import com.google.zxing.qrcode.QRCodeWriter;
//import com.visitor_x.service.QRService;
//import org.springframework.stereotype.Service;
//
//import java.io.IOException;
//import java.nio.file.FileSystems;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.UUID;
//
//@Service
//public class QRServiceImpl implements QRService {
//
//    private static final String QR_FOLDER = "uploads/qrcodes/";
//
//    @Override
//    public String generateQRCode(String text) throws WriterException, IOException {
//
//        // Create folder if it doesn't exist
//        Path folderPath = Paths.get(QR_FOLDER);
//        if (!Files.exists(folderPath)) {
//            Files.createDirectories(folderPath);
//        }
//
//        // Generate unique file name
//        String fileName = "QR_" + UUID.randomUUID() + ".png";
//        String filePath = QR_FOLDER + fileName;
//
//        QRCodeWriter qrCodeWriter = new QRCodeWriter();
//
//        BitMatrix bitMatrix = qrCodeWriter.encode(
//                text,
//                BarcodeFormat.QR_CODE,
//                300,
//                300
//        );
//
//        Path path = FileSystems.getDefault().getPath(filePath);
//
//        MatrixToImageWriter.writeToPath(
//                bitMatrix,
//                "PNG",
//                path
//        );
//
//        return filePath;
//    }
//}