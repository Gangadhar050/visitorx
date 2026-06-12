package com.visitor_x.serviceImpl;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.ExportService;
import com.visitor_x.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final ExportService exportService;

    @Override
    @Transactional
    public VisitorResponseDTO registerVisitor(VisitorRequestDTO request) {

        //Validates Email
        if (request.getEmail()==null ||
                !request.getEmail().matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            throw new IllegalArgumentException("Only Gmail addresses are allowed");
        }

        //validates Number
        if (request.getMobileNumber()==null ||
                !request.getMobileNumber().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException(
                    "Mobile number must contain exactly 10 digits");
        }


        visitorRepository.findByEmail(request.getEmail())

                .ifPresent(v -> {
                    throw new DuplicateResourceException(
                            "Email already registered");
                });

        visitorRepository.findByMobileNumber(request.getMobileNumber())
                .ifPresent(v -> {
                    throw new DuplicateResourceException(
                            "Mobile number already registered");
                });

        Visitor visitor = Visitor.builder()
                .name(request.getName())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .address(request.getAddress())
                .purposeOfVisit(request.getPurposeOfVisit())
                .photoUrl(request.getPhotoUrl())
                .build();

        Visitor saved = visitorRepository.save(visitor);
        exportService.autoSaveToFile();

        return toDTO(saved);
    }
    @Override
    public VisitorResponseDTO getVisitorById(Long visitorId) {
        Visitor visitor = visitorRepository.findById(visitorId)
                .orElseThrow(() -> new ResourceNotFoundException("Visitor not found with id: " + visitorId));
        return toDTO(visitor);
    }
    private VisitorResponseDTO toDTO(Visitor visitor) {
        return VisitorResponseDTO.builder()
                .visitorId(visitor.getVisitorId())
                .name(visitor.getName())
                .email(visitor.getEmail())
                .mobileNumber(visitor.getMobileNumber())
                .address(visitor.getAddress())
                .purposeOfVisit(visitor.getPurposeOfVisit())
                .photoUrl(visitor.getPhotoUrl())
                .visitDateTime(visitor.getVisitDateTime())
                .build();
    }
}