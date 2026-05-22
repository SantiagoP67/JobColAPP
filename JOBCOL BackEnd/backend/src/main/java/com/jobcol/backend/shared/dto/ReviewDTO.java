package com.jobcol.backend.shared.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDTO {

    private Long id;

    private Integer rating;

    private String comment;

    private String authorType;

    private String imageUrl;

    private LocalDateTime reviewDate;

    private Boolean visible;

    private Long reviewedUserId;

    private Long reviewerId;
}