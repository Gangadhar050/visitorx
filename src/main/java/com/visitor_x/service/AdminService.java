package com.visitor_x.service;

import com.visitor_x.dto.AdminRequestDTO;
import com.visitor_x.dto.AdminResponseDTO;

import java.util.List;

public interface AdminService {
    AdminResponseDTO createAdmin(AdminRequestDTO request);
    AdminResponseDTO getAdmin(Long id);
    List<AdminResponseDTO> getAllAdmins();
    void deleteAdmin(Long id);
}