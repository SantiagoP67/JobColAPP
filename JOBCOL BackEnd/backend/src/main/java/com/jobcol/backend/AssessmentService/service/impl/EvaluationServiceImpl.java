package com.jobcol.backend.AssessmentService.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jobcol.backend.AssessmentService.model.Answer;
import com.jobcol.backend.AssessmentService.model.Question;
import com.jobcol.backend.AssessmentService.model.QuestionType;
import com.jobcol.backend.AssessmentService.service.EvaluationService;
import com.jobcol.backend.AssessmentService.service.GeminiService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EvaluationServiceImpl implements EvaluationService {
    private final GeminiService GeminiService;

    @Override
    public double evaluate(List<Answer> answers, List<Question> questions) {

        double total = 0;

        for (Answer answer : answers) {

            Question q = questions.stream()
                    .filter(x -> x.getId().equals(answer.getQuestion().getId()))
                    .findFirst()
                    .orElseThrow();

            if (q.getType() == QuestionType.MULTIPLE) {

                if (q.getCorrectAnswer().equalsIgnoreCase(answer.getUserAnswer())) {
                    answer.setScore(1.0);
                    total += 1;
                } else {
                    answer.setScore(0.0);
                }

            } else {

                double score = evaluateOpenAI(q, answer.getUserAnswer());
                answer.setScore(score);
                total += score;
            }
        }

        return (total / questions.size()) * 100;
    }

    private double evaluateOpenAI(Question q, String userAnswer) {

        String prompt = """
        Evalúa esta respuesta:

        Pregunta: %s
        Respuesta esperada: %s
        Respuesta usuario: %s

        Devuelve SOLO un número entre 0 y 1.
        Sé estricto.
        """.formatted(q.getQuestionText(), q.getCorrectAnswer(), userAnswer);

        try {
            String response = GeminiService.chat(prompt);

            String number = response.replaceAll("[^0-9.]", "");

            return Double.parseDouble(number);

        } catch (Exception e) {
            return 0.5;
        }
    }
}
