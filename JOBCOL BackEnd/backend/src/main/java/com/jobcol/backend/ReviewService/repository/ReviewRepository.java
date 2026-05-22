package com.jobcol.backend.ReviewService.repository;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import com.jobcol.backend.ReviewService.model.Review;


public interface ReviewRepository extends JpaRepository<Review, Long> {

    List<Review> findByReviewedUser_Id(Long reviewedUserId);

    List<Review> findByReviewer_Id(Long reviewerId);
    
}
