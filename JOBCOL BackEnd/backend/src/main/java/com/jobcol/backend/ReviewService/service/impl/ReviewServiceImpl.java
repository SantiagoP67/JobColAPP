package com.jobcol.backend.ReviewService.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.jobcol.backend.ReviewService.model.Review;
import com.jobcol.backend.ReviewService.repository.ReviewRepository;
import com.jobcol.backend.ReviewService.service.ReviewService;
import com.jobcol.backend.UserService.model.EmployerProfile;
import com.jobcol.backend.UserService.model.Profile;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.EmployerProfileRepository;
import com.jobcol.backend.UserService.repository.ProfileRepository;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.ReviewDTO;
import com.jobcol.backend.shared.mappers.ReviewMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

        private final ReviewRepository reviewRepository;

        private final ProfileRepository profileRepository;

        private final EmployerProfileRepository employerProfileRepository;

        private final UserRepository userRepository;

        @Override
        public ReviewDTO createReview(
                ReviewDTO reviewDTO,
                MultipartFile image
        ) {

                User reviewedUser =
                        userRepository.findById(
                                reviewDTO.getReviewedUserId()
                        ).orElseThrow(() ->
                                new RuntimeException(
                                        "Reviewed user not found"
                                )
                        );

                User reviewer =
                        userRepository.findById(
                                reviewDTO.getReviewerId()
                        ).orElseThrow(() ->
                                new RuntimeException(
                                        "Reviewer not found"
                                )
                        );

                Review review =
                        ReviewMapper.toEntity(
                                reviewDTO
                        );

                review.setReviewedUser(
                        reviewedUser
                );

                review.setReviewer(
                        reviewer
                );

                review.setReviewDate(
                        LocalDateTime.now()
                );

                if (
                        image != null &&
                        !image.isEmpty()
                ) {

                String imageUrl =
                        saveImage(image);

                review.setImageUrl(
                        imageUrl
                );
                }

                Review saved =
                        reviewRepository.save(
                                review
                        );

                updateUserRating(
                        reviewedUser.getId()
                );

                return ReviewMapper.toDTO(
                        saved
                );
        }

        @Override
        public ReviewDTO updateReview(
                Long id,
                ReviewDTO reviewDTO
        ) {

                Review review =
                        reviewRepository.findById(id)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Review not found"
                                        )
                                );

                review.setRating(
                        reviewDTO.getRating()
                );

                review.setComment(
                        reviewDTO.getComment()
                );

                review.setVisible(
                        reviewDTO.getVisible()
                );

                review.setImageUrl(
                        reviewDTO.getImageUrl()
                );

                return ReviewMapper.toDTO(
                        reviewRepository.save(review)
                );
        }

        @Override
        public Optional<ReviewDTO> getReviewById(
                Long id
        ) {

                return reviewRepository.findById(id)
                        .map(ReviewMapper::toDTO);
        }

        @Override
        public List<ReviewDTO> getReviewsByReviewerId(
                Long reviewerId
        ) {

                return reviewRepository.findByReviewer_Id(reviewerId)
                        .stream()
                        .map(ReviewMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public List<ReviewDTO> getReviewsByReviewedUserId(
                Long reviewedUserId
        ) {

                return reviewRepository.findByReviewedUser_Id(
                                reviewedUserId
                        ).stream()
                        .map(ReviewMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public void deleteReview(
                Long id
        ) {

                reviewRepository.deleteById(id);
        }

        private void updateUserRating(
                Long reviewedUserId
        ) {

                List<Review> reviews =
                        reviewRepository
                                .findByReviewedUser_Id(
                                        reviewedUserId
                                );

                double average =
                        reviews.stream()
                                .mapToInt(Review::getRating)
                                .average()
                                .orElse(0.0);

                int totalReviews =
                        reviews.size();

                User user =
                        userRepository.findById(
                                reviewedUserId
                        ).orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

                if (
                        "TRABAJADOR".equals(
                                user.getRole()
                        )
                ) {

                Profile profile =
                        profileRepository
                                .findByUser_Id(
                                        reviewedUserId
                                )
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Profile not found"
                                        )
                                );

                profile.setAverageRating(
                        average
                );

                profile.setTotalReviews(
                        totalReviews
                );

                profileRepository.save(
                        profile
                );

                } else if (
                        "EMPLEADOR".equals(
                                user.getRole()
                        )
                ) {

                EmployerProfile profile =
                        employerProfileRepository
                                .findByUser_Id(
                                        reviewedUserId
                                )
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Employer profile not found"
                                        )
                                );

                profile.setAverageRating(
                        average
                );

                profile.setTotalReviews(
                        totalReviews
                );

                employerProfileRepository
                        .save(profile);
                }
        }

        private String saveImage(
                MultipartFile file
        ) {

                try {

                String fileName =
                        System.currentTimeMillis()
                        + "_"
                        + file.getOriginalFilename();

                Path uploadPath =
                        Paths.get(
                                "uploads/reviews"
                        );

                if (
                        !Files.exists(
                                uploadPath
                        )
                ) {

                        Files.createDirectories(
                                uploadPath
                        );
                }

                Path filePath =
                        uploadPath.resolve(
                                fileName
                        );

                Files.copy(
                        file.getInputStream(),
                        filePath,
                        StandardCopyOption.REPLACE_EXISTING
                );

                return "/uploads/reviews/"
                        + fileName;

                } catch (Exception e) {

                throw new RuntimeException(
                        "Error saving image",
                        e
                );
                }
        }
}