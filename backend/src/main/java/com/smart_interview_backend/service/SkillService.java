package com.smart_interview_backend.service;

import com.smart_interview_backend.dto.SkillRequest;
import com.smart_interview_backend.dto.SkillResponse;
import com.smart_interview_backend.entity.Skill;
import com.smart_interview_backend.entity.User;
import com.smart_interview_backend.repository.SkillRepository;
import com.smart_interview_backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SkillService {

    private final SkillRepository skillRepository;
    private final UserRepository userRepository;

    public SkillService(SkillRepository skillRepository, UserRepository userRepository) {
        this.skillRepository = skillRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public SkillResponse createSkill(SkillRequest request, String email) {
        User user = getUser(email);
        Skill skill = new Skill();
        skill.setSkillName(request.getSkillName());
        skill.setProficiency(request.getProficiency());
        skill.setUser(user);
        return toResponse(skillRepository.save(skill));
    }

    @Transactional(readOnly = true)
    public List<SkillResponse> getSkills(String email) {
        Long userId = getUser(email).getId();
        return skillRepository.findAllByUser_IdOrderByIdDesc(userId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public SkillResponse updateSkill(Long id, SkillRequest request, String email) {
        Long userId = getUser(email).getId();
        Skill skill = skillRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        skill.setSkillName(request.getSkillName());
        skill.setProficiency(request.getProficiency());
        return toResponse(skillRepository.save(skill));
    }

    @Transactional
    public void deleteSkill(Long id, String email) {
        Long userId = getUser(email).getId();
        Skill skill = skillRepository.findByIdAndUser_Id(id, userId)
                .orElseThrow(() -> new RuntimeException("Skill not found"));
        skillRepository.delete(skill);
    }

    private User getUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private SkillResponse toResponse(Skill s) {
        return new SkillResponse(s.getId(), s.getUser().getId(), s.getSkillName(), s.getProficiency());
    }
}
