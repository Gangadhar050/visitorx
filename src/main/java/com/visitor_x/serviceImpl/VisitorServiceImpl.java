package com.visitor_x.serviceImpl;

import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.dto.VisitorResponseDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.exception.ResourceNotFoundException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.ExportService;
import com.visitor_x.service.PhotoService;
import com.visitor_x.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;
    private final ExportService exportService;
    private final PhotoService photoService;
    @Override
    @Transactional
    public VisitorResponseDTO registerVisitorWithPhoto(VisitorRequestDTO request) {

        if (request.getEmail() == null ||
                !request.getEmail().matches("^[A-Za-z0-9+_.-]+@gmail\\.com$")) {
            throw new IllegalArgumentException("Only Gmail addresses are allowed");
        }

        if (request.getMobileNumber() == null ||
                !request.getMobileNumber().matches("^[0-9]{10}$")) {
            throw new IllegalArgumentException("Mobile number must contain exactly 10 digits");
        }

        if (request.getPhotoBase64() == null || request.getPhotoBase64().isBlank()) {
            throw new IllegalArgumentException("Photo is required");
        }

        visitorRepository.findByEmail(request.getEmail())
                .ifPresent(v -> { throw new DuplicateResourceException("Email already registered"); });

        visitorRepository.findByMobileNumber(request.getMobileNumber())
                .ifPresent(v -> { throw new DuplicateResourceException("Mobile number already registered"); });

        byte[] jpgPhotoData = photoService.convertBase64ToJpg(request.getPhotoBase64());

        Visitor visitor = Visitor.builder()
                .name(request.getName())
                .mobileNumber(request.getMobileNumber())
                .email(request.getEmail())
                .purposeOfVisit(request.getPurposeOfVisit())
                .photo(jpgPhotoData)
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

    @Override
    @Transactional
    public VisitorResponseDTO registerVisitor(VisitorRequestDTO request) {
        // existing implementation, unchanged
        return null; // keep your existing logic here
    }

    private VisitorResponseDTO toDTO(Visitor visitor) {
        String photoBase64 = null;
        if (visitor.getPhoto() != null && visitor.getPhoto().length > 0) {
            photoBase64 = "data:image/jpeg;base64," + Base64.getEncoder().encodeToString(visitor.getPhoto());
        }

        return VisitorResponseDTO.builder()
                .visitorId(visitor.getVisitorId())
                .name(visitor.getName())
                .email(visitor.getEmail())
                .mobileNumber(visitor.getMobileNumber())
                .purposeOfVisit(visitor.getPurposeOfVisit())
                .photoBase64(photoBase64)
                .visitDateTime(visitor.getVisitDateTime())
                .build();
    }
}