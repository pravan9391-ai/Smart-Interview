package com.smart_interview_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SkillRequest {

    @NotBlank
    private String skillName;

    private String proficiency;
}
