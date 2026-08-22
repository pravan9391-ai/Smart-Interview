package com.smart_interview_backend.controller;

import com.smart_interview_backend.dto.CandidateProfileRequest;
import com.smart_interview_backend.dto.CandidateProfileResponse;
import com.smart_interview_backend.service.CandidateProfileService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/candidate/profile")
public class CandidateProfileController {

    private final CandidateProfileService profileService;

    public CandidateProfileController(CandidateProfileService profileService) {
        this.profileService = profileService;
    }

    @PostMapping
    public CandidateProfileResponse createProfile(
            @Valid @RequestBody CandidateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return profileService.createOrUpdateProfile(request, userDetails.getUsername());
    }

    @PutMapping
    public CandidateProfileResponse updateProfile(
            @Valid @RequestBody CandidateProfileRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return profileService.createOrUpdateProfile(request, userDetails.getUsername());
    }

    @GetMapping
    public CandidateProfileResponse getProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return profileService.getProfile(userDetails.getUsername());
    }
}
