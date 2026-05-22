package com.jobcol.backend.UserService.model;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.jobcol.backend.OfferService.model.Offer;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "keycloak_user_id", length = 100)
    private String keycloakUserId;

    @Column(nullable = false, length = 320, unique = true)
    private String email;

    @Column(nullable = false, length = 120)
    private String username;

    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @Column(name = "cedula", nullable = false, unique = true, length = 20)
    private String cedula;

    @Column(name = "img_url", length = 500)
    private String imgUrl;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(name = "creation_date", nullable = false)
    private LocalDateTime creationDate = LocalDateTime.now();

    @Column(nullable = false, length = 50)
    private String role; // TRABAJADOR / EMPLEADOR

    @Column(name = "phone", length = 20)
    private String phone;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profile profile;

    @OneToMany(mappedBy = "employer", fetch = FetchType.LAZY)
    private Set<Offer> offers = new HashSet<>();
}
