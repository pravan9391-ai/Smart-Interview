package com.smart_interview_backend.dto;

import java.util.List;

public class ResumeParseResponse {

    private List<String> skills;

    private List<String> education;

    private List<String> projects;

    private List<String> experience;

    public ResumeParseResponse() {
    }

    public ResumeParseResponse(
            List<String> skills,
            List<String> education,
            List<String> projects,
            List<String> experience) {

        this.skills = skills;
        this.education = education;
        this.projects = projects;
        this.experience = experience;
    }

    public List<String> getSkills() {
        return skills;
    }

    public void setSkills(List<String> skills) {
        this.skills = skills;
    }

    public List<String> getEducation() {
        return education;
    }

    public void setEducation(List<String> education) {
        this.education = education;
    }

    public List<String> getProjects() {
        return projects;
    }

    public void setProjects(List<String> projects) {
        this.projects = projects;
    }

    public List<String> getExperience() {
        return experience;
    }

    public void setExperience(List<String> experience) {
        this.experience = experience;
    }
}
