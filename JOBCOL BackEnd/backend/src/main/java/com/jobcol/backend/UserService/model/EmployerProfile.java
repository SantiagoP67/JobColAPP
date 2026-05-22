package com.jobcol.backend.UserService.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "employer_profiles")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "company_name")
    private String companyName;

    @Column(length = 1000)
    private String description;

    @Column(length = 255)
    private String location;

    @Column(name = "average_rating")
    private Double averageRating;

    @Column(name = "total_jobs_posted")
    private Integer totalJobsPosted;

    @Column(name = "total_reviews")
    private Integer totalReviews;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, unique = true)
    private User user;
}
