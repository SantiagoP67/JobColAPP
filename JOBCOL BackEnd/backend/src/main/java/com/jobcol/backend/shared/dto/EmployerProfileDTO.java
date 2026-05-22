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
public class EmployerProfileDTO {
    private String companyName;
    private String description;
    private String location;
    private Double averageRating;
    private Integer totalReviews;
    private Integer totalJobsPosted;
}
