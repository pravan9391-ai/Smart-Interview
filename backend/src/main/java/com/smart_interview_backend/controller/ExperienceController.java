package com.smart_interview_backend.controller;

import com.smart_interview_backend.dto.ExperienceRequest;
import com.smart_interview_backend.dto.ExperienceResponse;
import com.smart_interview_backend.service.ExperienceService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidate/experience")
public class ExperienceController {

    private final ExperienceService experienceService;

    public ExperienceController(ExperienceService experienceService) {
        this.experienceService = experienceService;
    }

    @PostMapping
    public ExperienceResponse create(
            @Valid @RequestBody ExperienceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return experienceService.create(request, userDetails.getUsername());
    }

    @GetMapping
    public List<ExperienceResponse> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return experienceService.getAll(userDetails.getUsername());
    }

    @PutMapping("/{id}")
    public ExperienceResponse update(
            @PathVariable Long id,
            @Valid @RequestBody ExperienceRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return experienceService.update(id, request, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        experienceService.delete(id, userDetails.getUsername());
    }
}
