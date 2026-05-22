package com.jobcol.backend.AssessmentService.service.impl;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.AssessmentService.model.Answer;
import com.jobcol.backend.AssessmentService.model.JobAssessment;
import com.jobcol.backend.AssessmentService.model.Question;
import com.jobcol.backend.AssessmentService.model.QuestionType;
import com.jobcol.backend.AssessmentService.repository.AnswerRepository;
import com.jobcol.backend.AssessmentService.repository.AssessmentRepository;
import com.jobcol.backend.AssessmentService.repository.QuestionRepository;
import com.jobcol.backend.AssessmentService.service.AssessmentService;
import com.jobcol.backend.AssessmentService.service.EvaluationService;
import com.jobcol.backend.AssessmentService.service.QuestionGeneratorService;
import com.jobcol.backend.OfferService.model.Offer;
import com.jobcol.backend.OfferService.repository.OfferRepository;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.AnswerDTO;
import com.jobcol.backend.shared.dto.SubmitRequest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssessmentServiceImpl implements AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;

    private final QuestionGeneratorService generatorService;
    private final EvaluationService evaluationService;

    private final OfferRepository offerRepository;
    private final UserRepository userRepository;

    @Override
    public Long generateAssessment(Long offerId, Long userId) {

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new RuntimeException("Oferta no encontrada"));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        JobAssessment assessment = JobAssessment.builder()
                .offer(offer)
                .user(user)
                .createdAt(LocalDateTime.now())
                .completed(false)
                .build();

        assessment = assessmentRepository.save(assessment);

        List<Question> questions = generatorService.generate(offer.getDescription());

        for (Question q : questions) {
            q.setAssessment(assessment);
        }

        questionRepository.saveAll(questions);

        return assessment.getId();
    }

    @Override
    public List<Question> getQuestions(Long assessmentId) {
        return questionRepository.findByAssessmentId(assessmentId);
    }

    @Override
    public Double submitAnswers(Long assessmentId, SubmitRequest request) {

        JobAssessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new RuntimeException("Assessment no encontrado"));

        if (Boolean.TRUE.equals(assessment.getCompleted())) {
            throw new RuntimeException("El test ya fue completado");
        }

        List<Question> questions = questionRepository.findByAssessmentId(assessmentId);

        List<Answer> answers = new ArrayList<>();

        for (AnswerDTO dto : request.getAnswers()) {

            Question question = questions.stream()
                    .filter(q -> q.getId().equals(dto.getQuestionId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Pregunta no válida"));

            Answer answer = Answer.builder()
                    .question(question)
                    .userAnswer(dto.getAnswer())
                    .build();

            answers.add(answer);
        }

        
        double score = evaluationService.evaluate(answers, questions);

        answerRepository.saveAll(answers);

        long duration = Duration.between(
                assessment.getCreatedAt(),
                LocalDateTime.now()
        ).getSeconds();

        assessment.setScore(score);
        assessment.setLevel(getLevel(score));
        assessment.setCompleted(true);
        assessment.setDurationSeconds((int) duration);

        assessmentRepository.save(assessment);

        return score;
    }

    @Override
    public JobAssessment getResult(Long id) {
        return assessmentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Assessment no encontrado"));
    }

    private String getLevel(double score) {
        if (score >= 80) return "ALTO";
        if (score >= 50) return "MEDIO";
        return "BAJO";
    }
}
