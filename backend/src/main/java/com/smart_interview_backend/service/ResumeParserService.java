package com.smart_interview_backend.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.smart_interview_backend.entity.Education;
import com.smart_interview_backend.repository.EducationRepository;
import com.smart_interview_backend.repository.SkillRepository;

import com.smart_interview_backend.dto.ResumeParseResponse;
import com.smart_interview_backend.entity.Skill;
import com.smart_interview_backend.entity.User;

import com.smart_interview_backend.entity.Experience;
import com.smart_interview_backend.repository.ExperienceRepository;

import com.smart_interview_backend.entity.Project;
import com.smart_interview_backend.repository.ProjectRepository;

@Service
public class ResumeParserService {

    private final SkillRepository skillRepository;
    private final EducationRepository educationRepository;
    private final ExperienceRepository experienceRepository;
    private final ProjectRepository projectRepository;

    public ResumeParserService(
        SkillRepository skillRepository,
        EducationRepository educationRepository,
        ExperienceRepository experienceRepository,
        ProjectRepository projectRepository) {

    this.skillRepository = skillRepository;
    this.educationRepository = educationRepository;
    this.experienceRepository = experienceRepository;
    this.projectRepository = projectRepository;
    }

    public String extractText(MultipartFile file) throws IOException {

        try (InputStream inputStream = file.getInputStream()) {

            byte[] pdfBytes = inputStream.readAllBytes();

            try (PDDocument document = Loader.loadPDF(pdfBytes)) {

                PDFTextStripper stripper = new PDFTextStripper();

                return stripper.getText(document);
            }
        }
    }

    private String extractSection(
            String text,
            String startKeyword,
            String... endKeywords) {

        String upperText = text.toUpperCase();

        int start = upperText.indexOf(startKeyword);

        if (start == -1) {
            return "";
        }

        start += startKeyword.length();

        int end = text.length();

        for (String keyword : endKeywords) {

            int position =
                    upperText.indexOf(keyword.toUpperCase(), start);

            if (position != -1 && position < end) {
                end = position;
            }
        }

        return text.substring(start, end).trim();
    }


    // STEP 3: Parse Skills
    private List<String> parseSkills(String text) {

        String section = extractSection(
                text,
                "SKILLS",
                "EDUCATION",
                "EXPERIENCE",
                "PROJECTS"
        );

        return Arrays.stream(section.split("\\r?\\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .collect(Collectors.toList());
    }

    // eduction parser method
    private List<String> parseEducation(String text) {

    String section = extractSection(
            text,
            "EDUCATION",
            "EXPERIENCE",
            "PROJECTS",
            "SKILLS"
    );

    return Arrays.stream(section.split("\\r?\\n"))
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .collect(Collectors.toList());
    }

    // experience parser method
    private List<String> parseExperience(String text) {

    String section = extractSection(
            text,
            "EXPERIENCE",
            "EDUCATION",
            "PROJECTS",
            "SKILLS"
    );

    return Arrays.stream(section.split("\\r?\\n"))
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .collect(Collectors.toList());
    }

    // project parser method
    private List<String> parseProjects(String text) {

    String section = extractSection(
            text,
            "PROJECTS",
            "EDUCATION",
            "EXPERIENCE",
            "SKILLS"
    );

    return Arrays.stream(section.split("\\r?\\n"))
            .map(String::trim)
            .filter(line -> !line.isEmpty())
            .collect(Collectors.toList());
}



    //main parser method
    public ResumeParseResponse parseResume(MultipartFile file)
        throws IOException {

    String text = extractText(file);

    List<String> skills = parseSkills(text);

    List<String> education = parseEducation(text);

    List<String> experience = parseExperience(text);

    List<String> projects = parseProjects(text);

    return new ResumeParseResponse(
            skills,
            education,
            projects,
            experience
    );
    }

    private void saveSkills(List<String> parsedSkills, User user) {

    for (String skillName : parsedSkills) {

        Skill skill = new Skill();

        skill.setSkillName(skillName);

        skill.setUser(user);

        skillRepository.save(skill);
    }
    }


    private void saveEducation(
        List<String> parsedEducation,
        User user) {

    if (parsedEducation == null || parsedEducation.isEmpty()) {
        return;
    }

    Education education = new Education();

    if (parsedEducation.size() > 0) {
        education.setDegree(parsedEducation.get(0));
    }

    if (parsedEducation.size() > 1) {
        education.setInstitution(parsedEducation.get(1));
    }

    if (parsedEducation.size() > 2) {
        education.setFieldOfStudy(parsedEducation.get(2));
    }

    if (parsedEducation.size() > 3) {
        education.setStartYear(parsedEducation.get(3));
    }

    if (parsedEducation.size() > 4) {
        education.setEndYear(parsedEducation.get(4));
    }

    education.setUser(user);

    educationRepository.save(education);
    }


    private void saveExperience(
        List<String> parsedExperience,
        User user) {

    if (parsedExperience == null || parsedExperience.isEmpty()) {
        return;
    }

    Experience experience = new Experience();

    if (parsedExperience.size() > 0) {
        experience.setCompanyName(
                parsedExperience.get(0)
        );
    }

    if (parsedExperience.size() > 1) {
        experience.setJobTitle(
                parsedExperience.get(1)
        );
    }

    if (parsedExperience.size() > 2) {
        experience.setStartDate(
                parsedExperience.get(2)
        );
    }

    if (parsedExperience.size() > 3) {
        experience.setEndDate(
                parsedExperience.get(3)
        );
    }

    if (parsedExperience.size() > 4) {
        experience.setDescription(
                parsedExperience.get(4)
        );
    }

    experience.setUser(user);

    experienceRepository.save(experience);
    }

    private void saveProjects(
        List<String> parsedProjects,
        User user) {

    for (String projectText : parsedProjects) {

        Project project = new Project();

        project.setProjectName(projectText);

        project.setUser(user);

        projectRepository.save(project);
    }
    }


    public void parseAndSaveResume(
            MultipartFile file,
            User user) throws IOException {

        validatePdf(file);
        // 1. Extract text from PDF
        String text = extractText(file);

        // 2. Parse sections
        List<String> parsedSkills = parseSkills(text);

        List<String> parsedEducation = parseEducation(text);

        List<String> parsedExperience = parseExperience(text);

        List<String> parsedProjects = parseProjects(text);

        // 3. Save extracted data
        saveSkills(parsedSkills, user);

        saveEducation(parsedEducation, user);

        saveExperience(parsedExperience, user);

        saveProjects(parsedProjects, user);
    }

    private void validatePdf(MultipartFile file) {

    if (file == null || file.isEmpty()) {
        throw new RuntimeException(
                "Resume file is empty"
        );
    }

    if (!"application/pdf".equals(
            file.getContentType())) {

        throw new RuntimeException(
                "Only PDF files are allowed"
        );
    }

    if (file.getSize() > 5 * 1024 * 1024) {

        throw new RuntimeException(
                "Resume size must be less than 5 MB"
        );
    }
    }

}
