package com.jobcol.backend.OfferService.service;

import com.jobcol.backend.NotificationService.service.NotificationService;
import com.jobcol.backend.OfferService.model.Offer;
import com.jobcol.backend.OfferService.repository.OfferRepository;
import com.jobcol.backend.OfferService.service.impl.OfferServiceImpl;
import com.jobcol.backend.UserService.model.EmployerProfile;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.EmployerProfileRepository;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.OfferDTO;
import com.jobcol.backend.shared.mappers.OfferMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferServiceImplTest {

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmployerProfileRepository employerProfileRepository;

    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private OfferServiceImpl offerService;

    private User employer;
    private User worker;
    private Offer offer;
    private OfferDTO offerDTO;
    private EmployerProfile employerProfile;

    @BeforeEach
    void setUp() {

        employer = User.builder()
                .id(1L)
                .username("empleador")
                .email("empleador@test.com")
                .build();

        worker = User.builder()
                .id(2L)
                .username("worker")
                .email("worker@test.com")
                .build();

        offer = new Offer();
        offer.setId(1L);
        offer.setTitle("Java Developer");
        offer.setDescription("Backend");
        offer.setCategory("TI");
        offer.setLocation("Bogotá");
        offer.setSalaryRange(5000000);
        offer.setStatus("OPEN");
        offer.setPublicationDate(LocalDateTime.now());
        offer.setEmployer(employer);

        offerDTO = new OfferDTO(
                1L,
                "Java Developer",
                "Backend",
                "TI",
                "Bogotá",
                5000000,
                "OPEN",
                LocalDateTime.now(),
                1L,
                Set.of()
        );

        employerProfile = new EmployerProfile();
        employerProfile.setUser(employer);
        employerProfile.setTotalJobsPosted(0);
    }

    @Test
    void createOffer_shouldCreateOfferSuccessfully() {

        try (MockedStatic<OfferMapper> mapper =
                     mockStatic(OfferMapper.class)) {

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(employer));

            when(userRepository.findByRole("TRABAJADOR"))
                    .thenReturn(List.of(worker));

            when(employerProfileRepository.findByUser_Id(1L))
                    .thenReturn(Optional.of(employerProfile));

            when(offerRepository.findByEmployer_Id(1L))
                    .thenReturn(List.of());

            when(offerRepository.save(any(Offer.class)))
                    .thenReturn(offer);

            doNothing().when(notificationService)
                    .createNotificationsBatch(
                            anyList(),
                            anyString(),
                            anyString(),
                            any()
                    );

            doNothing().when(notificationService)
                    .sendEmail(
                            anyString(),
                            anyString(),
                            anyString()
                    );

            mapper.when(() -> OfferMapper.toEntity(offerDTO))
                    .thenReturn(offer);

            mapper.when(() -> OfferMapper.toDTO(any(Offer.class)))
                    .thenReturn(offerDTO);

            OfferDTO result = offerService.createOffer(offerDTO);

            assertThat(result).isNotNull();
            assertThat(result.title()).isEqualTo("Java Developer");

            verify(offerRepository).save(any(Offer.class));

            verify(notificationService)
                    .createNotificationsBatch(
                            anyList(),
                            anyString(),
                            anyString(),
                            any()
                    );

            verify(notificationService)
                    .sendEmail(
                            eq("worker@test.com"),
                            anyString(),
                            anyString()
                    );
        }
    }

    @Test
    void createOffer_whenEmployerNotFound_shouldThrowException() {

        when(userRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() ->
                offerService.createOffer(offerDTO)
        )
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("User not found");
    }

    @Test
    void createOffer_whenEmployerProfileNotFound_shouldThrowException() {

        try (MockedStatic<OfferMapper> mapper =
                     mockStatic(OfferMapper.class)) {

            when(userRepository.findById(1L))
                    .thenReturn(Optional.of(employer));

            mapper.when(() -> OfferMapper.toEntity(offerDTO))
                    .thenReturn(offer);

            when(employerProfileRepository.findByUser_Id(1L))
                    .thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    offerService.createOffer(offerDTO)
            )
                    .isInstanceOf(RuntimeException.class)
                    .hasMessageContaining("Employer profile not found");
        }
    }

    @Test
    void updateOffer_shouldUpdateOffer() {

        try (MockedStatic<OfferMapper> mapper =
                     mockStatic(OfferMapper.class)) {

            when(offerRepository.findById(1L))
                    .thenReturn(Optional.of(offer));

            when(offerRepository.save(any(Offer.class)))
                    .thenReturn(offer);

            mapper.when(() -> OfferMapper.toDTO(any(Offer.class)))
                    .thenReturn(offerDTO);

            OfferDTO result =
                    offerService.updateOffer(1L, offerDTO);

            assertThat(result).isNotNull();

            verify(offerRepository)
                    .save(any(Offer.class));
        }
    }

    @Test
    void getOfferById_shouldReturnOffer() {

        try (MockedStatic<OfferMapper> mapper =
                     mockStatic(OfferMapper.class)) {

            when(offerRepository.findById(1L))
                    .thenReturn(Optional.of(offer));

            mapper.when(() -> OfferMapper.toDTO(offer))
                    .thenReturn(offerDTO);

            Optional<OfferDTO> result =
                    offerService.getOfferById(1L);

            assertThat(result).isPresent();
        }
    }

    @Test
    void getOffersByUserId_shouldReturnOffers() {

        try (MockedStatic<OfferMapper> mapper =
                     mockStatic(OfferMapper.class)) {

            when(offerRepository.findByEmployer_Id(1L))
                    .thenReturn(List.of(offer));

            mapper.when(() -> OfferMapper.toDTO(offer))
                    .thenReturn(offerDTO);

            List<OfferDTO> result =
                    offerService.getOffersByUserId(1L);

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void getAllOffers_shouldReturnOffers() {

        try (MockedStatic<OfferMapper> mapper =
                     mockStatic(OfferMapper.class)) {

            when(offerRepository.findAll())
                    .thenReturn(List.of(offer));

            mapper.when(() -> OfferMapper.toDTO(offer))
                    .thenReturn(offerDTO);

            List<OfferDTO> result =
                    offerService.getAllOffers();

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void getActiveOffers_shouldReturnOpenOffers() {

        try (MockedStatic<OfferMapper> mapper =
                     mockStatic(OfferMapper.class)) {

            when(offerRepository.findByStatus("OPEN"))
                    .thenReturn(List.of(offer));

            mapper.when(() -> OfferMapper.toDTO(offer))
                    .thenReturn(offerDTO);

            List<OfferDTO> result =
                    offerService.getActiveOffers();

            assertThat(result).hasSize(1);
        }
    }

    @Test
    void closeOffer_shouldCloseOffer() {

        try (MockedStatic<OfferMapper> mapper =
                     mockStatic(OfferMapper.class)) {

            when(offerRepository.findById(1L))
                    .thenReturn(Optional.of(offer));

            when(offerRepository.save(any(Offer.class)))
                    .thenReturn(offer);

            OfferDTO closedDTO = new OfferDTO(
                    1L,
                    "Java Developer",
                    "Backend",
                    "TI",
                    "Bogotá",
                    5000000,
                    "CLOSED",
                    LocalDateTime.now(),
                    1L,
                    Set.of()
            );

            mapper.when(() -> OfferMapper.toDTO(any(Offer.class)))
                    .thenReturn(closedDTO);

            OfferDTO result =
                    offerService.closeOffer(1L);

            assertThat(result.status())
                    .isEqualTo("CLOSED");
        }
    }

    @Test
    void deleteOffer_shouldDeleteOffer() {

        doNothing().when(offerRepository)
                .deleteById(1L);

        offerService.deleteOffer(1L);

        verify(offerRepository)
                .deleteById(1L);
    }
}