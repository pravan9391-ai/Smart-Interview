package com.smart_interview_backend.controller;

import com.smart_interview_backend.dto.SkillRequest;
import com.smart_interview_backend.dto.SkillResponse;
import com.smart_interview_backend.service.SkillService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/candidate/skills")
public class SkillController {

    private final SkillService skillService;

    public SkillController(SkillService skillService) {
        this.skillService = skillService;
    }

    @PostMapping
    public SkillResponse createSkill(
            @Valid @RequestBody SkillRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return skillService.createSkill(request, userDetails.getUsername());
    }

    @GetMapping
    public List<SkillResponse> getSkills(
            @AuthenticationPrincipal UserDetails userDetails) {
        return skillService.getSkills(userDetails.getUsername());
    }

    @PutMapping("/{id}")
    public SkillResponse updateSkill(
            @PathVariable Long id,
            @Valid @RequestBody SkillRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return skillService.updateSkill(id, request, userDetails.getUsername());
    }

    @DeleteMapping("/{id}")
    public void deleteSkill(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        skillService.deleteSkill(id, userDetails.getUsername());
    }
}
