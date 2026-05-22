package com.jobcol.backend.AssessmentService.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.jobcol.backend.AssessmentService.model.JobAssessment;
import com.jobcol.backend.AssessmentService.model.Question;
import com.jobcol.backend.AssessmentService.service.AssessmentService;
import com.jobcol.backend.shared.dto.SubmitRequest;


@RestController
@RequestMapping("/assessments")
@RequiredArgsConstructor
public class AssessmentController {

    private final AssessmentService service;

    @PostMapping("/generate")
    public ResponseEntity<?> generate(
            @RequestParam Long offerId,
            @RequestParam Long userId) {

        try {
            Long assessmentId = service.generateAssessment(offerId, userId);

            return ResponseEntity.ok(assessmentId);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<?> getQuestions(@PathVariable Long id) {

        try {
            List<Question> questions = service.getQuestions(id);

            if (questions.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            return ResponseEntity.ok(questions);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submit(
            @PathVariable Long id,
            @RequestBody SubmitRequest request) {

        try {

            if (request.getAnswers() == null || request.getAnswers().isEmpty()) {
                return ResponseEntity.badRequest().body("Debe enviar respuestas");
            }

            Double score = service.submitAnswers(id, request);

            return ResponseEntity.ok(score);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/result")
    public ResponseEntity<?> getResult(@PathVariable Long id) {

        try {
            JobAssessment result = service.getResult(id);

            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
