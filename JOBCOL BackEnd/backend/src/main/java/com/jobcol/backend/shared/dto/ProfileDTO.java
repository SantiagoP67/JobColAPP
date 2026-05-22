    package com.jobcol.backend.shared.dto;

    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;
    import lombok.Builder;
    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public class ProfileDTO {
        private Long id;
        private String skills;
        private String experience;
        private String location;
        private Boolean visible;
        private Double averageRating;
        private Integer totalReviews;
        private Long userId;
    } 