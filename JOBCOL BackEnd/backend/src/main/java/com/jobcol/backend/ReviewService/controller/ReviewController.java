package com.jobcol.backend.ReviewService.controller;

import com.jobcol.backend.ReviewService.service.ReviewService;
import com.jobcol.backend.shared.dto.ReviewDTO;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;


    @PostMapping(
        consumes = "multipart/form-data"
    )
    public ResponseEntity<ReviewDTO> createReview(

        @RequestPart("review")
        ReviewDTO reviewDTO,

        @RequestPart(
            value = "image",
            required = false
        )
        MultipartFile image

    ) {

        ReviewDTO createdReview =
                reviewService.createReview(
                        reviewDTO,
                        image
                );

        return ResponseEntity.ok(
                createdReview
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long id,
            @RequestBody ReviewDTO reviewDTO) {

        ReviewDTO updatedReview = reviewService.updateReview(id, reviewDTO);
        return ResponseEntity.ok(updatedReview);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ReviewDTO> getReviewById(@PathVariable Long id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    
    @GetMapping("/contract/{reviewedUserId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByReviewedId(@PathVariable Long reviewedUserId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByReviewedUserId(reviewedUserId);
        return ResponseEntity.ok(reviews);
    }


    @GetMapping("/reviewed-user/{reviewerId}")
    public ResponseEntity<List<ReviewDTO>> getReviewsByReviewerId(@PathVariable Long reviewerId) {
        List<ReviewDTO> reviews = reviewService.getReviewsByReviewerId(reviewerId);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}