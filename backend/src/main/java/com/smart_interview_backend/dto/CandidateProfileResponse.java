package com.smart_interview_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CandidateProfileResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String phone;
    private String location;
    private String summary;
    private String dateOfBirth;
}
