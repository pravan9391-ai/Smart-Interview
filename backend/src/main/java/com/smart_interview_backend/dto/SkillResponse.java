package com.smart_interview_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SkillResponse {
    private Long id;
    private Long userId;
    private String skillName;
    private String proficiency;
}
