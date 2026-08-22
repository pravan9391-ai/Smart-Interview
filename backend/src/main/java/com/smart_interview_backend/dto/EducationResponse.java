package com.smart_interview_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EducationResponse {
    private Long id;
    private Long userId;
    private String degree;
    private String institution;
    private String fieldOfStudy;
    private String startYear;
    private String endYear;
}
