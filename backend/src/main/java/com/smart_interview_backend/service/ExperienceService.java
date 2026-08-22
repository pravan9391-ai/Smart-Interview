package com.smart_interview_backend.service;

import com.smart_interview_backend.dto.ExperienceRequest;
import com.smart_interview_backend.dto.ExperienceResponse;
import com.smart_interview_backend.entity.Experience;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.repository.ExperienceRepository;
import com.smart_interview_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ExperienceService {

    private final ExperienceRepository experienceRepository;
    private final UserRepository userRepository;

    public ExperienceService(ExperienceRepository experienceRepository, UserRepository userRepository) {
        this.experienceRepository = experienceRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public ExperienceResponse create(ExperienceRequest request, String email) {
        User user = getUser(email);
        Experience experience = new Experience();
        experience.setCompanyName(request.getCompanyName());
        experience.setJobTitle(request.getJobTitle());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setDescription(request.getDescription());
        experience.setUser(user);
        return toResponse(experienceRepository.save(experience));
    }

    @Transactional(readOnly = true)
    public List<ExperienceResponse> getAll(String email) {
        Long userId = getUser(email).getId();
        return experienceRepository.findAllByUser_IdOrderByIdDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ExperienceResponse update(Long id, ExperienceRequest request, String email) {
        Long userId = getUser(email).getId();
        Experience experience = experienceRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));
        experience.setCompanyName(request.getCompanyName());
        experience.setJobTitle(request.getJobTitle());
        experience.setStartDate(request.getStartDate());
        experience.setEndDate(request.getEndDate());
        experience.setDescription(request.getDescription());
        return toResponse(experienceRepository.save(experience));
    }

    @Transactional
    public void delete(Long id, String email) {
        Long userId = getUser(email).getId();
        Experience experience = experienceRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new RuntimeException("Experience not found"));
        experienceRepository.delete(experience);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private ExperienceResponse toResponse(Experience e) {
        return new ExperienceResponse(e.getId(), e.getUser().getId(), e.getCompanyName(),
                e.getJobTitle(), e.getStartDate(), e.getEndDate(), e.getDescription());
    }
}
