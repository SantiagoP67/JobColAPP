package com.jobcol.backend.AssessmentService.service;

import java.util.List;

import com.jobcol.backend.AssessmentService.model.Answer;
import com.jobcol.backend.AssessmentService.model.Question;

public interface EvaluationService {
    public double evaluate(List<Answer> answers, List<Question> questions);
}
