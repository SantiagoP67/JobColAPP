package com.jobcol.backend.ReviewService.model;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.jobcol.backend.HiringService.model.Contract;
import com.jobcol.backend.UserService.model.User;

@Entity
@Table(name = "reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer rating;

    @Column(length = 1000)
    private String comment;

    @Column(nullable = false, length = 50)
    private String authorType;

    private String imageUrl;

    private LocalDateTime reviewDate;

    @Column(nullable = false)
    private Boolean visible = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_user_id", nullable = false)
    private User reviewedUser;

    @ManyToOne
    @JoinColumn(name = "reviewer_id")
    private User reviewer;
}
