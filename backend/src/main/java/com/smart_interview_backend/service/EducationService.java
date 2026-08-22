package com.smart_interview_backend.service;

import com.smart_interview_backend.dto.EducationRequest;
import com.smart_interview_backend.dto.EducationResponse;
import com.smart_interview_backend.entity.Education;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.repository.EducationRepository;
import com.smart_interview_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EducationService {

    private final EducationRepository educationRepository;
    private final UserRepository userRepository;

    public EducationService(EducationRepository educationRepository, UserRepository userRepository) {
        this.educationRepository = educationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EducationResponse create(EducationRequest request, String email) {
        User user = getUser(email);
        Education education = new Education();
        education.setDegree(request.getDegree());
        education.setInstitution(request.getInstitution());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        education.setUser(user);
        return toResponse(educationRepository.save(education));
    }

    @Transactional(readOnly = true)
    public List<EducationResponse> getAll(String email) {
        Long userId = getUser(email).getId();
        return educationRepository.findAllByUser_IdOrderByIdDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public EducationResponse update(Long id, EducationRequest request, String email) {
        Long userId = getUser(email).getId();
        Education education = educationRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new RuntimeException("Education not found"));
        education.setDegree(request.getDegree());
        education.setInstitution(request.getInstitution());
        education.setFieldOfStudy(request.getFieldOfStudy());
        education.setStartYear(request.getStartYear());
        education.setEndYear(request.getEndYear());
        return toResponse(educationRepository.save(education));
    }

    @Transactional
    public void delete(Long id, String email) {
        Long userId = getUser(email).getId();
        Education education = educationRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new RuntimeException("Education not found"));
        educationRepository.delete(education);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private EducationResponse toResponse(Education e) {
        return new EducationResponse(e.getId(), e.getUser().getId(), e.getDegree(),
                e.getInstitution(), e.getFieldOfStudy(), e.getStartYear(), e.getEndYear());
    }
}
