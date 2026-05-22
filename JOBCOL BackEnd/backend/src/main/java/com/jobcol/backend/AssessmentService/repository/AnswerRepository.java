package com.jobcol.backend.AssessmentService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.AssessmentService.model.Answer;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    
}
