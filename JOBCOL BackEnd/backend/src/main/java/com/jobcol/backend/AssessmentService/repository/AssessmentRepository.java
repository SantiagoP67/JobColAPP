package com.jobcol.backend.AssessmentService.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jobcol.backend.AssessmentService.model.JobAssessment;

public interface AssessmentRepository extends JpaRepository<JobAssessment, Long> {

    
} 
