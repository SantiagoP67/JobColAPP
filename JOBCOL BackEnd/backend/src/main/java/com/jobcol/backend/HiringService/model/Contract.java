package com.jobcol.backend.HiringService.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.jobcol.backend.PostulationService.model.Postulation;
import com.jobcol.backend.ReviewService.model.Review;

@Entity
@Table(name = "contracts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startDate;

    private LocalDateTime endDate;

    private Integer agreedAmount;

    @Column(nullable = false)
    private Boolean workerFinished = false;

    @Column(nullable = false)
    private Boolean employerFinished = false;

    @Column(nullable = false, length = 50)
    private String status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "POSTULATION_ID", nullable = false, unique = true)
    private Postulation postulation;
    
}
