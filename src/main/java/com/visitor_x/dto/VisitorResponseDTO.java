package com.visitor_x.dto;



import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class VisitorResponseDTO {

    private Long visitorId;
    private String name;
    private String mobileNumber;
    private String photoUrl;
    private LocalDateTime visitDateTime;
}