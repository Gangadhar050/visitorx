package com.visitor_x.serviceImpl;
import com.visitor_x.dto.VisitorRequestDTO;
import com.visitor_x.entity.Visitor;
import com.visitor_x.exception.DuplicateResourceException;
import com.visitor_x.repository.VisitorRepository;
import com.visitor_x.service.VisitorService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VisitorServiceImpl implements VisitorService {

    private final VisitorRepository visitorRepository;


    @Override
    public Visitor registerVisitor(VisitorRequestDTO request) {

        visitorRepository.findByEmail(request.getEmail())
                .ifPresent(v -> {
                    throw new DuplicateResourceException(
                            "Email already registered");
                });

        visitorRepository.findByMobileNumber(
                        request.getMobileNumber())
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

        return visitorRepository.save(visitor);
    }
}