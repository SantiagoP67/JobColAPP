package com.jobcol.backend.AssessmentService.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.AssessmentService.service.GeminiService;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

@Service
public class GeminiServiceImpl implements GeminiService {

    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.url}")
    private String url;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String chat(String prompt) {
        try {
            // Gemini usa: { "contents": [ { "parts": [ { "text": "..." } ] } ] }
            Map<String, Object> part = new HashMap<>();
            part.put("text", prompt);

            Map<String, Object> content = new HashMap<>();
            content.put("parts", List.of(part));

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("contents", List.of(content));

            // Opcional: controlar temperatura
            Map<String, Object> generationConfig = new HashMap<>();
            generationConfig.put("temperature", 0.1);
            requestBody.put("generationConfig", generationConfig);

            String jsonBody = mapper.writeValueAsString(requestBody);

            System.out.println("===== REQUEST GEMINI =====");
            System.out.println(jsonBody);

            String fullUrl = url + "?key=" + apiKey;

            Request request = new Request.Builder()
                    .url(fullUrl)
                    .post(RequestBody.create(jsonBody, MediaType.parse("application/json")))
                    .build();

            Response response = client.newCall(request).execute();
            String json = response.body().string();

            System.out.println("===== RAW GEMINI RESPONSE =====");
            System.out.println(json);

            return extractContent(json);

        } catch (Exception e) {
            throw new RuntimeException("Error llamando a Gemini", e);
        }
    }

    private String extractContent(String json) {
        try {
            Map<String, Object> map = mapper.readValue(json, Map.class);

            // Si hay error, Gemini lo retorna en "error.message"
            if (map.containsKey("error")) {
                Map<String, Object> error = (Map<String, Object>) map.get("error");
                throw new RuntimeException("Gemini Error: " + error.get("message"));
            }

            // Estructura: candidates[0].content.parts[0].text
            List<Map<String, Object>> candidates =
                    (List<Map<String, Object>>) map.get("candidates");

            Map<String, Object> content =
                    (Map<String, Object>) candidates.get(0).get("content");

            List<Map<String, Object>> parts =
                    (List<Map<String, Object>>) content.get("parts");

            return (String) parts.get(0).get("text");

        } catch (Exception e) {
            throw new RuntimeException("Error extrayendo contenido de Gemini", e);
        }
    }
}