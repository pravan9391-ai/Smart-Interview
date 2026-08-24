package com.smart_interview_backend.dto;

import com.smart_interview_backend.entity.Resume;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ResumeResponse {

    private Long id;
    private String fileName;
    private String fileType;
    private Long fileSize;
    private LocalDateTime uploadedAt;

    public static ResumeResponse fromEntity(Resume resume) {
        return new ResumeResponse(
                resume.getId(),
                resume.getFileName(),
                resume.getFileType(),
                resume.getFileSize(),
                resume.getUploadedAt()
        );
    }
}
