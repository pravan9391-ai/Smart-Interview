package com.smart_interview_backend.controller;


import com.smart_interview_backend.dto.ResumeResponse;
import com.smart_interview_backend.entity.Resume;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.repository.UserRepository;
import com.smart_interview_backend.service.ResumeService;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/candidate/resume")
public class ResumeController {

    private final ResumeService resumeService;
    private final UserRepository userRepository;

    public ResumeController(
            ResumeService resumeService,
            UserRepository userRepository) {

        this.resumeService = resumeService;
        this.userRepository = userRepository;
    }


    // =========================
    // UPLOAD
    // =========================

    @PostMapping("/upload")
    public ResponseEntity<?> uploadResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {

            User user = getLoggedInUser(authentication);

            Resume resume =
                    resumeService.uploadResume(file, user);

            return ResponseEntity.ok(ResumeResponse.fromEntity(resume));

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================
    // GET MY RESUMES
    // =========================

    @GetMapping
    public ResponseEntity<?> getMyResumes(
            Authentication authentication) {

        User user = getLoggedInUser(authentication);

        List<Resume> resumes =
                resumeService.getUserResumes(user);

        List<ResumeResponse> response = resumes.stream()
                .map(ResumeResponse::fromEntity)
                .toList();

        return ResponseEntity.ok(response);
    }


    // =========================
    // DOWNLOAD
    // =========================

    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadResume(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User user = getLoggedInUser(authentication);

            Resume resume =
                    resumeService.getResume(id);

            byte[] data =
                    resumeService.downloadResume(id, user);

            ByteArrayResource resource =
                    new ByteArrayResource(data);

            return ResponseEntity.ok()
                    .contentType(
                            MediaType.APPLICATION_PDF
                    )
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" +
                                    resume.getFileName() +
                                    "\""
                    )
                    .body(resource);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================
    // DELETE
    // =========================

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteResume(
            @PathVariable Long id,
            Authentication authentication) {

        try {

            User user = getLoggedInUser(authentication);

            resumeService.deleteResume(id, user);

            return ResponseEntity.ok(
                    "Resume deleted successfully"
            );

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body(e.getMessage());
        }
    }


    // =========================
    // GET LOGGED-IN USER
    // =========================

    private User getLoggedInUser(
            Authentication authentication) {

        String email = authentication.getName();

        return userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException(
                                "User not found"
                        ));
    }
}