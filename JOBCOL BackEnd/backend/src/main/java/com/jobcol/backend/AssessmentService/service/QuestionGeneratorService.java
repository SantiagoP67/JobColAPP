package com.jobcol.backend.AssessmentService.service;

import java.util.List;

import com.jobcol.backend.AssessmentService.model.Question;

public interface QuestionGeneratorService {
    List<Question> generate(String jobDescription);
}
