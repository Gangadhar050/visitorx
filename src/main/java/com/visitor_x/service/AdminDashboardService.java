package com.visitor_x.service;

import com.visitor_x.dto.DashboardResponse;
import com.visitor_x.entity.Visitor;

import java.util.List;

public interface AdminDashboardService {

    DashboardResponse getDashboard();

    List<Visitor> getAllVisitors();

    List<Visitor> searchVisitors(String keyword);

    Visitor getVisitor(Long id);

    void deleteVisitor(Long id);
}