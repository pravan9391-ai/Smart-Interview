package com.smart_interview_backend.controller;

import com.smart_interview_backend.dto.ResumeParseResponse;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.repository.UserRepository;
import com.smart_interview_backend.service.ResumeParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resume-parser")
public class ResumeParserController {

    private final ResumeParserService resumeParserService;
    private final UserRepository userRepository;

    public ResumeParserController(
            ResumeParserService resumeParserService,
            UserRepository userRepository) {
        this.resumeParserService = resumeParserService;
        this.userRepository = userRepository;
    }

    /**
     * Extract raw PDF text only. No database write.
     */
    @PostMapping("/extract-text")
    public ResponseEntity<?> extractText(
            @RequestParam("file") MultipartFile file) {

        try {
            return ResponseEntity.ok(resumeParserService.extractText(file));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                    .body(error("Unable to parse resume: " + e.getMessage()));
        }
    }

    /**
     * Parse and SAVE the resume data for the currently authenticated user.
     *
     * This endpoint fixes the original issue: /parse previously called only
     * parseResume(), which returned JSON but never called any repository.save().
     */
    @PostMapping("/parse")
    public ResponseEntity<?> parseAndSaveResume(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        try {
            User user = getAuthenticatedUser(authentication);

            ResumeParseResponse response =
                    resumeParserService.parseAndSaveResume(file, user);

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(error(e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(error("Resume parsing/storage failed: " + e.getMessage()));
        }
    }

    /**
     * Explicit alias for clients that want an endpoint whose name clearly
     * indicates that parsing also persists the result.
     */
    @PostMapping("/parse-and-save")
    public ResponseEntity<?> parseAndSaveResumeAlias(
            @RequestParam("file") MultipartFile file,
            Authentication authentication) {

        return parseAndSaveResume(file, authentication);
    }

    private User getAuthenticatedUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalArgumentException("Authentication is required");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new IllegalArgumentException("Authenticated user not found"));
    }

    private ErrorResponse error(String message) {
        return new ErrorResponse(message);
    }

    private record ErrorResponse(String message) {
    }
}
