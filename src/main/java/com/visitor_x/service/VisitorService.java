package com.visitor_x.service;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.entity.Visitor;

public interface VisitorService {

    Visitor registerVisitor(VisitorRequestDTO request);
}