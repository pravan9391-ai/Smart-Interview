package com.smart_interview_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ExperienceRequest {

    @NotBlank
    private String companyName;

    @NotBlank
    private String jobTitle;

    private String startDate;
    private String endDate;
    private String description;
}
