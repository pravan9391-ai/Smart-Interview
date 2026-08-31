package com.smart_interview_backend.dto;

import java.util.List;

public class AIQuestionResponse {

    private List<String> questions;

    public AIQuestionResponse() {
    }

    public AIQuestionResponse(List<String> questions) {
        this.questions = questions;
    }

    public List<String> getQuestions() {
        return questions;
    }

    public void setQuestions(List<String> questions) {
        this.questions = questions;
    }
}