package com.smart_interview_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EducationRequest {

    @NotBlank
    private String degree;

    @NotBlank
    private String institution;

    private String fieldOfStudy;
    private String startYear;
    private String endYear;
}
