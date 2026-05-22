package com.jobcol.backend.ReviewService.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcol.backend.ReviewService.service.ReviewService;
import com.jobcol.backend.shared.dto.ReviewDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
@AutoConfigureMockMvc(addFilters = false)
class ReviewControllerTest {

        @Autowired private MockMvc mockMvc;
        @MockBean  private ReviewService reviewService;
        @Autowired private ObjectMapper objectMapper;

        private ReviewDTO reviewDTO;

        @BeforeEach
        void setUp() {
                reviewDTO = ReviewDTO.builder()
                        .id(1L)
                        .rating(5)
                        .comment("Excelente trabajador")
                        .authorType("EMPLEADOR")
                        .reviewDate(LocalDateTime.now())
                        .visible(true)
                        .reviewedUserId(2L)
                        .reviewerId(1L)
                        .build();
        }

        @Test
        void createReview_sinImagen_shouldReturn200() throws Exception {
                when(reviewService.createReview(any(ReviewDTO.class), isNull())).thenReturn(reviewDTO);

                MockMultipartFile reviewPart = new MockMultipartFile(
                        "review", "", MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(reviewDTO)
                );

                mockMvc.perform(multipart("/reviews")
                                .file(reviewPart))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.rating").value(5));
        }

        @Test
        void createReview_conImagen_shouldReturn200() throws Exception {
                when(reviewService.createReview(any(ReviewDTO.class), any())).thenReturn(reviewDTO);

                MockMultipartFile reviewPart = new MockMultipartFile(
                        "review", "", MediaType.APPLICATION_JSON_VALUE,
                        objectMapper.writeValueAsBytes(reviewDTO)
                );

                MockMultipartFile image = new MockMultipartFile(
                        "image", "foto.jpg", "image/jpeg", "data".getBytes()
                );

                mockMvc.perform(multipart("/reviews")
                                .file(reviewPart)
                                .file(image))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.rating").value(5));
        }

        @Test
        void updateReview_shouldReturn200() throws Exception {
                when(reviewService.updateReview(eq(1L), any(ReviewDTO.class))).thenReturn(reviewDTO);

                mockMvc.perform(put("/reviews/1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(reviewDTO)))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        void getReviewById_whenExists_shouldReturn200() throws Exception {
                when(reviewService.getReviewById(1L)).thenReturn(Optional.of(reviewDTO));

                mockMvc.perform(get("/reviews/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.rating").value(5));
        }

        @Test
        void getReviewById_whenNotExists_shouldReturn404() throws Exception {
                when(reviewService.getReviewById(99L)).thenReturn(Optional.empty());

                mockMvc.perform(get("/reviews/99"))
                        .andExpect(status().isNotFound());
        }

        @Test
        void getReviewsByReviewedUserId_shouldReturn200() throws Exception {
                when(reviewService.getReviewsByReviewedUserId(2L)).thenReturn(List.of(reviewDTO));

                mockMvc.perform(get("/reviews/reviewed-user/2"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].reviewedUserId").value(2));
        }

        @Test
        void getReviewsByReviewerId_shouldReturn200() throws Exception {
                when(reviewService.getReviewsByReviewerId(1L)).thenReturn(List.of(reviewDTO));

                mockMvc.perform(get("/reviews/reviewer/1"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$[0].reviewerId").value(1));
        }

        @Test
        void deleteReview_shouldReturn204() throws Exception {
                doNothing().when(reviewService).deleteReview(1L);

                mockMvc.perform(delete("/reviews/1"))
                        .andExpect(status().isNoContent());
        }
}