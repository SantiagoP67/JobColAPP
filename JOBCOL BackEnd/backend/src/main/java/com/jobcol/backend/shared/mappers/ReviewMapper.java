package com.jobcol.backend.shared.mappers;

import com.jobcol.backend.ReviewService.model.Review;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.shared.dto.ReviewDTO;

public class ReviewMapper {

    public static ReviewDTO toDTO(Review review) {

        if (review == null) {
            return null;
        }

        Long reviewedUserId =
                review.getReviewedUser() != null
                        ? review.getReviewedUser().getId()
                        : null;

        Long reviewerId =
                review.getReviewer() != null
                        ? review.getReviewer().getId()
                        : null;

        return ReviewDTO.builder()

                .id(review.getId())

                .rating(review.getRating())

                .comment(review.getComment())

                .authorType(review.getAuthorType())

                .reviewDate(review.getReviewDate())

                .visible(review.getVisible())

                .reviewedUserId(reviewedUserId)

                .reviewerId(reviewerId)

                .imageUrl(review.getImageUrl())

                .build();
    }

  
    public static Review toEntity(ReviewDTO dto) {

        if (dto == null) {
            return null;
        }

        User reviewedUser = null;

        if (dto.getReviewedUserId() != null) {

            reviewedUser = new User();

            reviewedUser.setId(
                    dto.getReviewedUserId()
            );
        }

        User reviewer = null;

        if (dto.getReviewerId() != null) {

            reviewer = new User();

            reviewer.setId(
                    dto.getReviewerId()
            );
        }

        return Review.builder()

                .id(dto.getId())

                .rating(dto.getRating())

                .comment(dto.getComment())

                .authorType(dto.getAuthorType())

                .reviewDate(dto.getReviewDate())

                .visible(dto.getVisible())

                .reviewedUser(reviewedUser)

                .reviewer(reviewer)

                .imageUrl(dto.getImageUrl())

                .build();
    }
}