package com.visitor_x.dto;


import com.visitor_x.enums.PurposeOfVisit;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VisitorRequestDTO {


    @NotBlank
    private String name;

    @NotBlank
    private String mobileNumber;

    @Email
    @NotBlank
    private String email;

    private String address;

    private PurposeOfVisit purposeOfVisit;

    @NotBlank
    private String photoUrl;

}