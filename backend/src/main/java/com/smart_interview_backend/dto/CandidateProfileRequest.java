package com.smart_interview_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CandidateProfileRequest {

    @NotBlank
    private String fullName;

    private String phone;
    private String location;
    private String summary;
    private String dateOfBirth;
}
