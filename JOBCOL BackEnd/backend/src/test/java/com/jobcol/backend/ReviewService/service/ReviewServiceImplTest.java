package com.jobcol.backend.ReviewService.service;

import com.jobcol.backend.ReviewService.model.Review;
import com.jobcol.backend.ReviewService.repository.ReviewRepository;
import com.jobcol.backend.ReviewService.service.impl.ReviewServiceImpl;
import com.jobcol.backend.UserService.model.EmployerProfile;
import com.jobcol.backend.UserService.model.Profile;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.EmployerProfileRepository;
import com.jobcol.backend.UserService.repository.ProfileRepository;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.ReviewDTO;
import com.jobcol.backend.shared.mappers.ReviewMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceImplTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private ProfileRepository profileRepository;
    @Mock private EmployerProfileRepository employerProfileRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User worker;
    private User employer;
    private Review review;
    private ReviewDTO reviewDTO;
    private Profile profile;
    private EmployerProfile employerProfile;

    @BeforeEach
    void setUp() {
        worker = User.builder()
                .id(1L)
                .firstName("Carlos")
                .lastName("Lopez")
                .role("TRABAJADOR")
                .build();

        employer = User.builder()
                .id(2L)
                .firstName("Juan")
                .lastName("Perez")
                .role("EMPLEADOR")
                .build();

        review = new Review();
        review.setId(1L);
        review.setRating(5);
        review.setComment("Excelente trabajo");
        review.setAuthorType("EMPLEADOR");
        review.setReviewDate(LocalDateTime.now());
        review.setVisible(true);
        review.setReviewedUser(worker);
        review.setReviewer(employer);

        reviewDTO = ReviewDTO.builder()
                .id(1L)
                .rating(5)
                .comment("Excelente trabajo")
                .authorType("EMPLEADOR")
                .reviewDate(LocalDateTime.now())
                .visible(true)
                .reviewedUserId(1L)
                .reviewerId(2L)
                .build();

        profile = new Profile();
        profile.setAverageRating(0.0);
        profile.setTotalReviews(0);

        employerProfile = new EmployerProfile();
        employerProfile.setAverageRating(0.0);
        employerProfile.setTotalReviews(0);
    }

    // ── createReview sin imagen ──────────────────────────────────

    @Test
    void createReview_withoutImage_shouldCreateSuccessfully() {
        try (MockedStatic<ReviewMapper> mapper = mockStatic(ReviewMapper.class)) {
            when(userRepository.findById(1L)).thenReturn(Optional.of(worker));
            when(userRepository.findById(2L)).thenReturn(Optional.of(employer));
            mapper.when(() -> ReviewMapper.toEntity(reviewDTO)).thenReturn(review);
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewRepository.findByReviewedUser_Id(1L)).thenReturn(List.of(review));
            when(profileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));
            mapper.when(() -> ReviewMapper.toDTO(review)).thenReturn(reviewDTO);

            ReviewDTO result = reviewService.createReview(reviewDTO, null);

            assertThat(result).isNotNull();
            assertThat(result.getRating()).isEqualTo(5);
            verify(reviewRepository).save(any(Review.class));
            verify(profileRepository).save(profile);
            assertThat(profile.getAverageRating()).isEqualTo(5.0);
            assertThat(profile.getTotalReviews()).isEqualTo(1);
        }
    }

    // ── createReview con imagen ──────────────────────────────────

    @Test
    void createReview_withImage_shouldSaveImageAndCreate() {
        try (MockedStatic<ReviewMapper> mapper = mockStatic(ReviewMapper.class)) {
            MockMultipartFile image = new MockMultipartFile(
                    "image", "foto.jpg", "image/jpeg", "contenido".getBytes()
            );

            when(userRepository.findById(1L)).thenReturn(Optional.of(worker));
            when(userRepository.findById(2L)).thenReturn(Optional.of(employer));
            mapper.when(() -> ReviewMapper.toEntity(reviewDTO)).thenReturn(review);
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            when(reviewRepository.findByReviewedUser_Id(1L)).thenReturn(List.of(review));
            when(profileRepository.findByUser_Id(1L)).thenReturn(Optional.of(profile));
            mapper.when(() -> ReviewMapper.toDTO(review)).thenReturn(reviewDTO);

            ReviewDTO result = reviewService.createReview(reviewDTO, image);

            assertThat(result).isNotNull();
            assertThat(review.getImageUrl()).contains("/uploads/reviews/");
            verify(reviewRepository).save(any(Review.class));
        }
    }

    // ── createReview errores ─────────────────────────────────────

    @Test
    void createReview_whenReviewedUserNotFound_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(reviewDTO, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Reviewed user not found");
    }

    @Test
    void createReview_whenReviewerNotFound_shouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(worker));
        when(userRepository.findById(2L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.createReview(reviewDTO, null))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Reviewer not found");
    }

    // ── updateUserRating para EMPLEADOR ──────────────────────────

    @Test
    void createReview_forEmployer_shouldUpdateEmployerProfile() {
        try (MockedStatic<ReviewMapper> mapper = mockStatic(ReviewMapper.class)) {
            ReviewDTO employerReviewDTO = ReviewDTO.builder()
                    .id(2L).rating(4).comment("Buen empleador")
                    .authorType("TRABAJADOR").visible(true)
                    .reviewedUserId(2L).reviewerId(1L).build();

            Review employerReview = new Review();
            employerReview.setId(2L);
            employerReview.setRating(4);
            employerReview.setReviewedUser(employer);
            employerReview.setReviewer(worker);

            when(userRepository.findById(2L)).thenReturn(Optional.of(employer));
            when(userRepository.findById(1L)).thenReturn(Optional.of(worker));
            mapper.when(() -> ReviewMapper.toEntity(employerReviewDTO)).thenReturn(employerReview);
            when(reviewRepository.save(any(Review.class))).thenReturn(employerReview);
            when(reviewRepository.findByReviewedUser_Id(2L)).thenReturn(List.of(employerReview));
            when(employerProfileRepository.findByUser_Id(2L)).thenReturn(Optional.of(employerProfile));
            mapper.when(() -> ReviewMapper.toDTO(employerReview)).thenReturn(employerReviewDTO);

            ReviewDTO result = reviewService.createReview(employerReviewDTO, null);

            assertThat(result).isNotNull();
            verify(employerProfileRepository).save(employerProfile);
            assertThat(employerProfile.getAverageRating()).isEqualTo(4.0);
            assertThat(employerProfile.getTotalReviews()).isEqualTo(1);
        }
    }

    // ── updateReview ─────────────────────────────────────────────

    @Test
    void updateReview_shouldUpdateSuccessfully() {
        try (MockedStatic<ReviewMapper> mapper = mockStatic(ReviewMapper.class)) {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            when(reviewRepository.save(any(Review.class))).thenReturn(review);
            mapper.when(() -> ReviewMapper.toDTO(review)).thenReturn(reviewDTO);

            ReviewDTO result = reviewService.updateReview(1L, reviewDTO);

            assertThat(result).isNotNull();
            assertThat(result.getRating()).isEqualTo(5);
            verify(reviewRepository).save(review);
        }
    }

    @Test
    void updateReview_whenNotFound_shouldThrowException() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reviewService.updateReview(99L, reviewDTO))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Review not found");
    }

    // ── getReviewById ────────────────────────────────────────────

    @Test
    void getReviewById_whenExists_shouldReturnDTO() {
        try (MockedStatic<ReviewMapper> mapper = mockStatic(ReviewMapper.class)) {
            when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
            mapper.when(() -> ReviewMapper.toDTO(review)).thenReturn(reviewDTO);

            Optional<ReviewDTO> result = reviewService.getReviewById(1L);

            assertThat(result).isPresent();
            assertThat(result.get().getId()).isEqualTo(1L);
        }
    }

    @Test
    void getReviewById_whenNotExists_shouldReturnEmpty() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<ReviewDTO> result = reviewService.getReviewById(99L);

        assertThat(result).isEmpty();
    }

    // ── getReviewsByReviewerId ───────────────────────────────────

    @Test
    void getReviewsByReviewerId_shouldReturnList() {
        try (MockedStatic<ReviewMapper> mapper = mockStatic(ReviewMapper.class)) {
            when(reviewRepository.findByReviewer_Id(2L)).thenReturn(List.of(review));
            mapper.when(() -> ReviewMapper.toDTO(review)).thenReturn(reviewDTO);

            List<ReviewDTO> result = reviewService.getReviewsByReviewerId(2L);

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void getReviewsByReviewerId_whenNone_shouldReturnEmptyList() {
        when(reviewRepository.findByReviewer_Id(99L)).thenReturn(List.of());

        List<ReviewDTO> result = reviewService.getReviewsByReviewerId(99L);

        assertThat(result).isEmpty();
    }

    // ── getReviewsByReviewedUserId ───────────────────────────────

    @Test
    void getReviewsByReviewedUserId_shouldReturnList() {
        try (MockedStatic<ReviewMapper> mapper = mockStatic(ReviewMapper.class)) {
            when(reviewRepository.findByReviewedUser_Id(1L)).thenReturn(List.of(review));
            mapper.when(() -> ReviewMapper.toDTO(review)).thenReturn(reviewDTO);

            List<ReviewDTO> result = reviewService.getReviewsByReviewedUserId(1L);

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void getReviewsByReviewedUserId_whenNone_shouldReturnEmptyList() {
        when(reviewRepository.findByReviewedUser_Id(99L)).thenReturn(List.of());

        List<ReviewDTO> result = reviewService.getReviewsByReviewedUserId(99L);

        assertThat(result).isEmpty();
    }

    // ── deleteReview ─────────────────────────────────────────────

    @Test
    void deleteReview_shouldCallDeleteById() {
        doNothing().when(reviewRepository).deleteById(1L);

        reviewService.deleteReview(1L);

        verify(reviewRepository).deleteById(1L);
    }
}