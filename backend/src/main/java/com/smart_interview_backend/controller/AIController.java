package com.smart_interview_backend.controller;

import com.smart_interview_backend.dto.AIQuestionRequest;
import com.smart_interview_backend.dto.AIQuestionResponse;
import com.smart_interview_backend.service.GeminiService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/ai")
public class AIController {

private final GeminiService geminiService;

public AIController(GeminiService geminiService) {
        this.geminiService = geminiService;
}

@PostMapping("/generate-questions")
public ResponseEntity<AIQuestionResponse> generateQuestions(
        @RequestBody AIQuestionRequest request) {
        System.out.println("1. Request received");

        String prompt = buildPrompt(request);
        System.out.println("2. Prompt created: " + prompt);

        System.out.println("3. Calling Gemini...");

        String aiResponse =
                geminiService.generateQuestions(prompt);
        System.out.println("3.1. Gemini response received: " + aiResponse);
        
        System.out.println("4. Gemini response received");

        List<String> questions = Arrays.stream(
                aiResponse.split("\\r?\\n")
        )
        .map(String::trim)
        .filter(line -> !line.isEmpty())
        .map(line -> line.replaceFirst("^\\d+[.)]\\s*", ""))
        .toList();
          System.out.println("5. Questions processed");

        return ResponseEntity.ok(
                new AIQuestionResponse(questions)
        );
}

private String buildPrompt(AIQuestionRequest request) {

        return """
                You are an expert technical interviewer.

                Generate %d technical interview questions.

                Job Role:
                %s

                Skills:
                %s

                Experience:
                %s

                Requirements:
                - Questions must be relevant to the job role.
                - Questions must match the candidate's skills.
                - Questions should be technically meaningful.
                - Mix basic, intermediate and advanced questions.
                - Do not provide answers.
                - Return only the questions.
                - Number each question.

                """.formatted(
                request.getNumberOfQuestions(),
                request.getRole(),
                String.join(", ", request.getSkills()),
                request.getExperience()
        );
}
}