package com.jobcol.backend.ReviewService.service;

import java.util.List;
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;

import com.jobcol.backend.shared.dto.ReviewDTO;

public interface ReviewService {
    ReviewDTO createReview(ReviewDTO reviewDTO,MultipartFile image);

    ReviewDTO updateReview(Long id, ReviewDTO reviewDTO);

    Optional<ReviewDTO> getReviewById(Long id);


    List<ReviewDTO> getReviewsByReviewerId(Long reviewerId);

    void deleteReview(Long id);

    List<ReviewDTO> getReviewsByReviewedUserId(Long reviewedUserId);
}
