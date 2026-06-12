package com.visitor_x.service;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;

public interface VisitorService {

    VisitorResponseDTO registerVisitor(VisitorRequestDTO request);

    VisitorResponseDTO getVisitorById(Long visitorId);

    /**
     * Register visitor with photo upload
     * @param request VisitorRequestDTO containing visitor details and photo file
     * @return VisitorResponseDTO with visitor details and photo in base64
     */
    VisitorResponseDTO registerVisitorWithPhoto(VisitorRequestDTO request);
}