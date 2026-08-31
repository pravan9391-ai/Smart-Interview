package com.smart_interview_backend.service;

import com.smart_interview_backend.dto.ResumeParseResponse;
import com.smart_interview_backend.entity.Education;
import com.smart_interview_backend.entity.Experience;
import com.smart_interview_backend.entity.Project;
import com.smart_interview_backend.entity.Skill;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.repository.EducationRepository;
import com.smart_interview_backend.repository.ExperienceRepository;
import com.smart_interview_backend.repository.ProjectRepository;
import com.smart_interview_backend.repository.SkillRepository;
import jakarta.transaction.Transactional;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class ResumeParserService {

    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

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

    /**
     * Extract text from a PDF resume.
     */
    public String extractText(MultipartFile file) throws IOException {
        validatePdf(file);

        byte[] pdfBytes = file.getBytes();

        try (PDDocument document = Loader.loadPDF(pdfBytes)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    /**
     * Parse a named section without relying on case-sensitive headings.
     */
    private String extractSection(
            String text,
            String startKeyword,
            String... endKeywords) {

        if (text == null || text.isBlank()) {
            return "";
        }

        String upperText = text.toUpperCase(Locale.ROOT);
        String start = startKeyword.toUpperCase(Locale.ROOT);

        int startIndex = upperText.indexOf(start);
        if (startIndex == -1) {
            return "";
        }

        int contentStart = startIndex + start.length();
        int contentEnd = text.length();

        for (String endKeyword : endKeywords) {
            int position = upperText.indexOf(
                    endKeyword.toUpperCase(Locale.ROOT),
                    contentStart
            );

            if (position != -1 && position < contentEnd) {
                contentEnd = position;
            }
        }

        if (contentStart >= contentEnd) {
            return "";
        }

        return text.substring(contentStart, contentEnd).trim();
    }

    private List<String> parseLines(String section) {
        if (section == null || section.isBlank()) {
            return List.of();
        }

        return Arrays.stream(section.split("\\R"))
                .map(String::trim)
                .filter(line -> !line.isBlank())
                .distinct()
                .collect(Collectors.toList());
    }

    private List<String> parseSkills(String text) {
        return parseLines(extractSection(
                text,
                "SKILLS",
                "EDUCATION",
                "EXPERIENCE",
                "PROJECTS"
        ));
    }

    private List<String> parseEducation(String text) {
        return parseLines(extractSection(
                text,
                "EDUCATION",
                "EXPERIENCE",
                "PROJECTS",
                "SKILLS"
        ));
    }

    private List<String> parseExperience(String text) {
        return parseLines(extractSection(
                text,
                "EXPERIENCE",
                "EDUCATION",
                "PROJECTS",
                "SKILLS"
        ));
    }

    private List<String> parseProjects(String text) {
        return parseLines(extractSection(
                text,
                "PROJECTS",
                "EDUCATION",
                "EXPERIENCE",
                "SKILLS"
        ));
    }

    /**
     * Parse only. This method intentionally does not write to the database.
     * It is useful for clients that only need the extracted JSON.
     */
    public ResumeParseResponse parseResume(MultipartFile file) throws IOException {
        String text = extractText(file);
        return buildParseResponse(text);
    }

    /**
     * Parse and persist all supported resume data for the authenticated user.
     *
     * The whole operation is transactional: if one database write fails,
     * none of the parsed records are committed.
     */
    @Transactional
    public ResumeParseResponse parseAndSaveResume(
            MultipartFile file,
            User user) throws IOException {

        validatePdf(file);

        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("Authenticated user is required");
        }

        String text = extractText(file);
        ResumeParseResponse response = buildParseResponse(text);

        saveSkills(response.getSkills(), user);
        saveEducation(response.getEducation(), user);
        saveExperience(response.getExperience(), user);
        saveProjects(response.getProjects(), user);

        // Flush here so a database constraint/connection error is reported
        // by this request rather than later when the transaction is committed.
        skillRepository.flush();
        educationRepository.flush();
        experienceRepository.flush();
        projectRepository.flush();

        return response;
    }

    private ResumeParseResponse buildParseResponse(String text) {
        return new ResumeParseResponse(
                parseSkills(text),
                parseEducation(text),
                parseProjects(text),
                parseExperience(text)
        );
    }

    private void saveSkills(List<String> parsedSkills, User user) {
        if (parsedSkills == null) {
            return;
        }

        for (String skillName : parsedSkills) {
            String value = cleanValue(skillName);
            if (value == null) {
                continue;
            }

            Skill skill = new Skill();
            skill.setSkillName(value);
            skill.setUser(user);
            skillRepository.save(skill);
        }
    }

    /**
     * Education has non-null degree and institution columns.
     * Therefore an incomplete parsed block must not be inserted.
     */
    private void saveEducation(List<String> parsedEducation, User user) {
        if (parsedEducation == null || parsedEducation.size() < 2) {
            return;
        }

        String degree = cleanValue(parsedEducation.get(0));
        String institution = cleanValue(parsedEducation.get(1));

        if (degree == null || institution == null) {
            return;
        }

        Education education = new Education();
        education.setDegree(degree);
        education.setInstitution(institution);
        education.setFieldOfStudy(valueAt(parsedEducation, 2));
        education.setStartYear(valueAt(parsedEducation, 3));
        education.setEndYear(valueAt(parsedEducation, 4));
        education.setUser(user);

        educationRepository.save(education);
    }

    /**
     * Experience has non-null companyName and jobTitle columns.
     * Therefore an incomplete parsed block must not be inserted.
     */
    private void saveExperience(List<String> parsedExperience, User user) {
        if (parsedExperience == null || parsedExperience.size() < 2) {
            return;
        }

        String companyName = cleanValue(parsedExperience.get(0));
        String jobTitle = cleanValue(parsedExperience.get(1));

        if (companyName == null || jobTitle == null) {
            return;
        }

        Experience experience = new Experience();
        experience.setCompanyName(companyName);
        experience.setJobTitle(jobTitle);
        experience.setStartDate(valueAt(parsedExperience, 2));
        experience.setEndDate(valueAt(parsedExperience, 3));
        experience.setDescription(valueAt(parsedExperience, 4));
        experience.setUser(user);

        experienceRepository.save(experience);
    }

    private void saveProjects(List<String> parsedProjects, User user) {
        if (parsedProjects == null) {
            return;
        }

        for (String projectText : parsedProjects) {
            String value = cleanValue(projectText);
            if (value == null) {
                continue;
            }

            Project project = new Project();
            project.setProjectName(value);
            project.setUser(user);
            projectRepository.save(project);
        }
    }

    private String valueAt(List<String> values, int index) {
        if (values == null || index >= values.size()) {
            return null;
        }
        return cleanValue(values.get(index));
    }

    private String cleanValue(String value) {
        if (value == null) {
            return null;
        }

        String cleaned = value.trim();
        return cleaned.isBlank() ? null : cleaned;
    }

    private void validatePdf(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Resume file is empty");
        }

        String filename = file.getOriginalFilename();
        String contentType = file.getContentType();

        boolean pdfByExtension = filename != null
                && filename.toLowerCase(Locale.ROOT).endsWith(".pdf");

        boolean pdfByContentType = "application/pdf".equalsIgnoreCase(contentType);

        if (!pdfByExtension && !pdfByContentType) {
            throw new IllegalArgumentException("Only PDF files are allowed");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new IllegalArgumentException("Resume size must be less than 5 MB");
        }
    }
}
