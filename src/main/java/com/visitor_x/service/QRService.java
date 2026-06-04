//package com.visitor_x.service;
//
//import com.google.zxing.WriterException;
//import java.io.IOException;
//
//public interface QRService {
//
//    String generateQRCode(String text)
//            throws WriterException, IOException;
//}

package com.visitor_x.service;

import com.google.zxing.WriterException;
import java.io.IOException;

public interface QRService {

    byte[] generateQRCode(String text)
            throws WriterException, IOException;
}