package com.smart_interview_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExperienceResponse {
    private Long id;
    private Long userId;
    private String companyName;
    private String jobTitle;
    private String startDate;
    private String endDate;
    private String description;
}
