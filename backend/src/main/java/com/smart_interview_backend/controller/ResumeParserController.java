package com.smart_interview_backend.controller;

import com.smart_interview_backend.dto.ResumeParseResponse;
import com.smart_interview_backend.service.ResumeParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resume-parser")
public class ResumeParserController {

    private final ResumeParserService resumeParserService;

    public ResumeParserController(
            ResumeParserService resumeParserService) {

        this.resumeParserService = resumeParserService;
    }

    @PostMapping("/extract-text")
    public ResponseEntity<String> extractText(
            @RequestParam("file") MultipartFile file) {

        try {

            String text = resumeParserService.extractText(file);

            return ResponseEntity.ok(text);

        } catch (Exception e) {

            return ResponseEntity
                    .badRequest()
                    .body("Unable to parse resume: " + e.getMessage());
        }
    }

    @PostMapping("/parse")
    public ResponseEntity<ResumeParseResponse> parseResume(
            @RequestParam("file") MultipartFile file) {

        try {

            ResumeParseResponse response =
                    resumeParserService.parseResume(file);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            return ResponseEntity.badRequest().build();
        }
}

}
