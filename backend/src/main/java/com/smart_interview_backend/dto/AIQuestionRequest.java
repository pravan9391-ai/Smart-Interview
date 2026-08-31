package com.smart_interview_backend.dto;

import java.util.List;

public class AIQuestionRequest {

    private String role;

    private List<String> skills;

    private String experience;

    private int numberOfQuestions;

    public AIQuestionRequest() {
    }

    public AIQuestionRequest(String role, List<String> skills,
                            String experience, int numberOfQuestions) {
        this.role = role;
        this.skills = skills;
        this.experience = experience;
        this.numberOfQuestions = numberOfQuestions;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public String getExperience() {
        return experience;
    }

    public void setExperience(String experience) {
        this.experience = experience;
    }

    public int getNumberOfQuestions() {
        return numberOfQuestions;
    }

    public void setNumberOfQuestions(int numberOfQuestions) {
        this.numberOfQuestions = numberOfQuestions;
    }
}