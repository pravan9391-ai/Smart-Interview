package com.smart_interview_backend.repository;


import com.smart_interview_backend.entity.Resume;
import com.smart_interview_backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ResumeRepository extends JpaRepository<Resume, Long> {

    List<Resume> findByUser(User user);
}