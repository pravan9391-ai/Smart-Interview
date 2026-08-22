package com.smart_interview_backend.controller;

import com.smart_interview_backend.dto.EducationRequest;
import com.smart_interview_backend.dto.EducationResponse;
import com.smart_interview_backend.service.EducationService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidate/education")
public class EducationController {

    private final EducationService educationService;

    public EducationController(EducationService educationService) {
        this.educationService = educationService;
    }

    @PostMapping
    public EducationResponse create(
            @Valid @RequestBody EducationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return educationService.create(request, userDetails.getUsername());
    }

    @GetMapping
    public List<EducationResponse> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return educationService.getAll(userDetails.getUsername());
    }

    @PutMapping("/{id}")
    public EducationResponse update(
            @PathVariable Long id,
            @Valid @RequestBody EducationRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return educationService.update(id, request, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public void delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        educationService.delete(id, userDetails.getUsername());
    }
}
