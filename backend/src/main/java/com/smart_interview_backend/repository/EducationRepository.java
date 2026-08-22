package com.smart_interview_backend.repository;

import com.smart_interview_backend.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EducationRepository extends JpaRepository<Education, Long> {
    List<Education> findAllByUser_IdOrderByIdDesc(Long userId);
    Optional<Education> findByIdAndUser_Id(Long id, Long userId);
}
