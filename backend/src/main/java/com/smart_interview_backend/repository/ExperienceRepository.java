package com.smart_interview_backend.repository;

import com.smart_interview_backend.entity.Experience;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExperienceRepository extends JpaRepository<Experience, Long> {
    List<Experience> findAllByUser_IdOrderByIdDesc(Long userId);
    Optional<Experience> findByIdAndUser_Id(Long id, Long userId);
}
