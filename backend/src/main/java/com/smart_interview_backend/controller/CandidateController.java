package com.smart_interview_backend.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/candidate")
public class CandidateController {

    @GetMapping("/profile")
    public String profile() {

        return "Candidate profile";
    }
}
