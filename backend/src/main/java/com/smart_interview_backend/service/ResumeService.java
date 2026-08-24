package com.smart_interview_backend.service;


import com.smart_interview_backend.entity.Resume;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.repository.ResumeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;

    @Value("${resume.upload-dir:upload/resume}")
    private String uploadDirectoryPath;

    private Path getUploadDirectory() {
        return Paths.get(uploadDirectoryPath);
    }

    public ResumeService(ResumeRepository resumeRepository) {
        this.resumeRepository = resumeRepository;
    }

    // =========================
    // UPLOAD RESUME
    // =========================

    public Resume uploadResume(MultipartFile file, User user)
            throws IOException {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        String contentType = file.getContentType();
        
        
        String fileName1 = file.getOriginalFilename();

        if (fileName1 == null || !fileName1.toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("Only PDF files are allowed");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("File size must be less than 5MB");
        }

        Path uploadDirectory = getUploadDirectory();
        Files.createDirectories(uploadDirectory);

        String originalFileName = file.getOriginalFilename();

        String fileName =
                System.currentTimeMillis() + "_" + originalFileName;

        Path filePath = uploadDirectory.resolve(fileName);

        Files.copy(
                file.getInputStream(),
                filePath,
                StandardCopyOption.REPLACE_EXISTING
        );

        Resume resume = new Resume();

        resume.setFileName(originalFileName);
        resume.setFilePath(filePath.toString());
        resume.setFileType(contentType);
        resume.setFileSize(file.getSize());
        resume.setUploadedAt(LocalDateTime.now());
        resume.setUser(user);

        return resumeRepository.save(resume);
    }


    // =========================
    // GET USER RESUMES
    // =========================

    public List<Resume> getUserResumes(User user) {

        return resumeRepository.findByUser(user);
    }


    // =========================
    // FIND RESUME
    // =========================

    public Resume getResume(Long id) {

        return resumeRepository.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Resume not found"));
    }


    // =========================
    // DOWNLOAD
    // =========================

    public byte[] downloadResume(Long id, User user)
            throws IOException {

        Resume resume = getResume(id);

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to access this resume"
            );
        }

        Path path = Paths.get(resume.getFilePath());

        return Files.readAllBytes(path);
    }


    // =========================
    // DELETE
    // =========================

    public void deleteResume(Long id, User user)
            throws IOException {

        Resume resume = getResume(id);

        if (!resume.getUser().getId().equals(user.getId())) {
            throw new RuntimeException(
                    "You are not allowed to delete this resume"
            );
        }

        Path path = Paths.get(resume.getFilePath());

        Files.deleteIfExists(path);

        resumeRepository.delete(resume);
    }
}