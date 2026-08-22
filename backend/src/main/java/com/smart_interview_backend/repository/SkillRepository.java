package com.smart_interview_backend.repository;

import com.smart_interview_backend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SkillRepository extends JpaRepository<Skill, Long> {
    List<Skill> findAllByUser_IdOrderByIdDesc(Long userId);
    Optional<Skill> findByIdAndUser_Id(Long id, Long userId);
}
