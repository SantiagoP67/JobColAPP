package com.jobcol.backend.AssessmentService.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.AssessmentService.model.Question;
import com.jobcol.backend.AssessmentService.model.QuestionType;
import com.jobcol.backend.AssessmentService.service.GeminiService;
import com.jobcol.backend.AssessmentService.service.QuestionGeneratorService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionGeneratorServiceImpl implements QuestionGeneratorService {

private final GeminiService GeminiService;
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Question> generate(String jobDescription) {

        String prompt = buildPrompt(jobDescription);

        String content = GeminiService.chat(prompt);

        try {
            // 🔥 LIMPIEZA CLAVE
            content = cleanJson(content);

            // 🔥 DEBUG (puedes quitarlo luego)
            System.out.println("===== RESPUESTA IA LIMPIA =====");
            System.out.println(content);

            List<Map<String, Object>> list =
                    mapper.readValue(content, List.class);

            List<Question> questions = new ArrayList<>();

            for (Map<String, Object> q : list) {

                Question question = new Question();

                question.setQuestionText((String) q.get("question"));

                question.setType(
                        QuestionType.valueOf(((String) q.get("type")).toUpperCase())
                );

                question.setCorrectAnswer((String) q.get("answer"));

                // 🔥 SOLO SI ES MULTIPLE
                if (q.containsKey("options") && q.get("options") != null) {
                    question.setOptions(
                            mapper.writeValueAsString(q.get("options"))
                    );
                }

                questions.add(question);
            }

            return questions;

        } catch (Exception e) {

            System.out.println("===== ERROR PARSEANDO IA =====");
            System.out.println(content);

            throw new RuntimeException("Error parseando JSON IA", e);
        }
    }

    private String buildPrompt(String jobDescription) {
        return """
        Genera EXACTAMENTE 5 preguntas basadas en este trabajo:

        "%s"

        REGLAS OBLIGATORIAS:
        - SOLO responde JSON válido
        - NO expliques nada
        - NO agregues texto antes o después
        - NO uses ```json ni ```
        - 3 preguntas MULTIPLE con options
        - 2 preguntas OPEN sin options

        FORMATO EXACTO:
        [
          {
            "question": "texto",
            "type": "MULTIPLE",
            "options": ["a","b","c"],
            "answer": "respuesta correcta"
          },
          {
            "question": "texto",
            "type": "OPEN",
            "answer": "respuesta esperada"
          }
        ]
        """.formatted(jobDescription);
    }


    private String cleanJson(String content) {

        if (content == null) return "";

        return content
                .replace("```json", "")
                .replace("```", "")
                .trim();
    }
}
