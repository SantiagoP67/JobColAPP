package com.jobcol.backend.PostulationService.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import com.jobcol.backend.HiringService.model.Contract;
import com.jobcol.backend.OfferService.model.Offer;
import com.jobcol.backend.UserService.model.User;

@Entity
@Table(name = "postulations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Postulation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String status;

    @Column(nullable = false)
    private LocalDateTime applicationDate;

    private int calification;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKER_ID", nullable = false)
    private User worker;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "OFFER_ID", nullable = false)
    private Offer offer;

    @OneToOne(mappedBy = "postulation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Contract contract;
}
