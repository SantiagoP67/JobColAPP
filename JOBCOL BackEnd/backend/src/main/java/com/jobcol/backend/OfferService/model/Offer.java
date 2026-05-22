package com.jobcol.backend.OfferService.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.jobcol.backend.PostulationService.model.Postulation;
import com.jobcol.backend.UserService.model.User;

@Entity
@Table(name = "offers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Offer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(length = 255)
    private String category;

    @Column(length = 255)
    private String location;

    private Integer salaryRange; // revisar bien que funcione como minimo y maximo 

    @Column(nullable = false, length = 50)
    private String status;

    private LocalDateTime publicationDate;
    //falta tipo de pago 
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employer_id", nullable = false)
    private User employer;

    @OneToMany(mappedBy = "offer", fetch = FetchType.LAZY)
    private Set<Postulation> postulations = new HashSet<>();

}
