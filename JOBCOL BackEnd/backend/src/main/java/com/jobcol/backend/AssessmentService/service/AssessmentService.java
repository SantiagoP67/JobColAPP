package com.jobcol.backend.AssessmentService.service;

import java.util.List;

import com.jobcol.backend.AssessmentService.model.JobAssessment;
import com.jobcol.backend.AssessmentService.model.Question;
import com.jobcol.backend.shared.dto.SubmitRequest;

public interface AssessmentService {
    Long generateAssessment(Long offerId, Long userId);
    List<Question> getQuestions(Long assessmentId);
    Double submitAnswers(Long assessmentId, SubmitRequest request);
    JobAssessment getResult(Long id);
}
