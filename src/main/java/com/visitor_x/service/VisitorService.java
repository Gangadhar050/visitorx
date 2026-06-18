package com.visitor_x.service;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.enums.PurposeOfVisit;

public interface VisitorService {

//    VisitorResponseDTO registerVisitor(VisitorRequestDTO request);

    VisitorResponseDTO getVisitorById(Long visitorId);


    VisitorResponseDTO registerVisitorWithPhoto(VisitorRequestDTO request, PurposeOfVisit purpose);
}