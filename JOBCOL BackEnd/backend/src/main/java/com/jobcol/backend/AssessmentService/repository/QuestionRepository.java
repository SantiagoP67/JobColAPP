package com.jobcol.backend.AssessmentService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.AssessmentService.model.Question;

public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByAssessmentId(Long assessmentId);

    
} 
