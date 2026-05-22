package com.jobcol.backend.PostulationService.service;

import com.jobcol.backend.NotificationService.model.NotificationType;
import com.jobcol.backend.NotificationService.service.NotificationService;
import com.jobcol.backend.OfferService.model.Offer;
import com.jobcol.backend.OfferService.repository.OfferRepository;
import com.jobcol.backend.PostulationService.model.Postulation;
import com.jobcol.backend.PostulationService.repository.PostulationRepository;
import com.jobcol.backend.PostulationService.service.impl.PostulationServiceImpl;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.OfferDTO;
import com.jobcol.backend.shared.dto.PostulationDTO;
import com.jobcol.backend.shared.mappers.PostulationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PostulationServiceImplTest {

        @Mock private NotificationService notificationService;
        @Mock private PostulationRepository postulationRepository;
        @Mock private OfferRepository offerRepository;
        @Mock private UserRepository userRepository;

        @InjectMocks
        private PostulationServiceImpl postulationService;

        private Offer offer;
        private User employer;
        private User worker;
        private Postulation postulation;
        private PostulationDTO postulationDTO;

        @BeforeEach
        void setUp() {
                employer = User.builder()
                        .id(1L)
                        .firstName("Juan")
                        .lastName("Perez")
                        .email("empresa@test.com")
                        .build();

                worker = User.builder()
                        .id(2L)
                        .firstName("Carlos")
                        .lastName("Lopez")
                        .email("worker@test.com")
                        .build();

                offer = new Offer();
                offer.setId(1L);
                offer.setTitle("Java Developer");
                offer.setLocation("Bogotá");
                offer.setEmployer(employer);
                offer.setPostulations(new HashSet<>());

                postulation = Postulation.builder()
                        .id(1L)
                        .offer(offer)
                        .worker(worker)
                        .status("PENDING")
                        .applicationDate(LocalDateTime.now())
                        .build();

                OfferDTO offerDTO = new OfferDTO(
                        1L, "Java Developer", "Backend", "TI",
                        "Bogotá", 5000000, "OPEN", LocalDateTime.now(), 1L, Set.of()
                );

                postulationDTO = new PostulationDTO(
                        1L, "PENDING", LocalDateTime.now(), 2L, 0, offerDTO, null
                );
        }

        @Test
        void createPostulation_shouldCreateSuccessfully() {
                try (MockedStatic<PostulationMapper> mapper = mockStatic(PostulationMapper.class)) {
                when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
                when(userRepository.findById(2L)).thenReturn(Optional.of(worker));
                when(postulationRepository.save(any(Postulation.class))).thenReturn(postulation);
                when(notificationService.createNotification(
                        anyLong(), anyString(), anyString(), any(NotificationType.class)
                )).thenReturn(null);
                doNothing().when(notificationService).sendEmail(anyString(), anyString(), anyString());
                mapper.when(() -> PostulationMapper.toDTO(any(Postulation.class))).thenReturn(postulationDTO);

                PostulationDTO result = postulationService.createPostulation(postulationDTO);

                assertThat(result).isNotNull();
                verify(postulationRepository).save(any(Postulation.class));
                verify(notificationService).createNotification(
                        anyLong(), anyString(), anyString(), any(NotificationType.class)
                );
                verify(notificationService).sendEmail(anyString(), anyString(), anyString());
                }
        }

        @Test
        void createPostulation_whenOfferNotFound_shouldThrowException() {
                when(offerRepository.findById(1L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> postulationService.createPostulation(postulationDTO))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("Offer not found");
        }

        @Test
        void createPostulation_whenWorkerNotFound_shouldThrowException() {
                when(offerRepository.findById(1L)).thenReturn(Optional.of(offer));
                when(userRepository.findById(2L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> postulationService.createPostulation(postulationDTO))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("User not found");
        }

        @Test
        void updateStatus_shouldUpdatePostulation() {
                try (MockedStatic<PostulationMapper> mapper = mockStatic(PostulationMapper.class)) {
                when(postulationRepository.findById(1L)).thenReturn(Optional.of(postulation));
                when(postulationRepository.save(any(Postulation.class))).thenReturn(postulation);
                mapper.when(() -> PostulationMapper.toDTO(any(Postulation.class))).thenReturn(postulationDTO);

                PostulationDTO result = postulationService.updateStatus(1L, "ACCEPTED");

                assertThat(result).isNotNull();
                verify(postulationRepository).save(any(Postulation.class));
                }
        }

        @Test
        void updateStatus_whenNotFound_shouldThrowException() {
                when(postulationRepository.findById(99L)).thenReturn(Optional.empty());

                assertThatThrownBy(() -> postulationService.updateStatus(99L, "ACCEPTED"))
                        .isInstanceOf(RuntimeException.class)
                        .hasMessageContaining("Postulation not found");
        }

        @Test
        void getPostulationById_whenExists_shouldReturnPostulation() {
                try (MockedStatic<PostulationMapper> mapper = mockStatic(PostulationMapper.class)) {
                when(postulationRepository.findById(1L)).thenReturn(Optional.of(postulation));
                mapper.when(() -> PostulationMapper.toDTO(postulation)).thenReturn(postulationDTO);

                Optional<PostulationDTO> result = postulationService.getPostulationById(1L);

                assertThat(result).isPresent();
                }
        }

        @Test
        void getPostulationById_whenNotExists_shouldReturnEmpty() {
                when(postulationRepository.findById(99L)).thenReturn(Optional.empty());

                Optional<PostulationDTO> result = postulationService.getPostulationById(99L);

                assertThat(result).isEmpty();
        }

        @Test
        void getPostulationsByUserId_shouldReturnList() {
                try (MockedStatic<PostulationMapper> mapper = mockStatic(PostulationMapper.class)) {
                when(postulationRepository.findByWorker_Id(2L)).thenReturn(List.of(postulation));
                mapper.when(() -> PostulationMapper.toDTO(postulation)).thenReturn(postulationDTO);

                List<PostulationDTO> result = postulationService.getPostulationsByUserId(2L);

                assertThat(result).hasSize(1);
                }
        }

        @Test
        void getPostulationsByUserId_whenNone_shouldReturnEmptyList() {
                when(postulationRepository.findByWorker_Id(99L)).thenReturn(List.of());

                List<PostulationDTO> result = postulationService.getPostulationsByUserId(99L);

                assertThat(result).isEmpty();
        }

        @Test
        void getPostulationsByJobOfferId_shouldReturnList() {
                try (MockedStatic<PostulationMapper> mapper = mockStatic(PostulationMapper.class)) {
                when(postulationRepository.findByOffer_Id(1L)).thenReturn(List.of(postulation));
                mapper.when(() -> PostulationMapper.toDTO(postulation)).thenReturn(postulationDTO);

                List<PostulationDTO> result = postulationService.getPostulationsByJobOfferId(1L);

                assertThat(result).hasSize(1);
                }
        }

        @Test
        void getPostulationsByJobOfferId_whenNone_shouldReturnEmptyList() {
                when(postulationRepository.findByOffer_Id(99L)).thenReturn(List.of());

                List<PostulationDTO> result = postulationService.getPostulationsByJobOfferId(99L);

                assertThat(result).isEmpty();
        }

        @Test
        void getAllPostulations_shouldReturnAll() {
                try (MockedStatic<PostulationMapper> mapper = mockStatic(PostulationMapper.class)) {
                when(postulationRepository.findAll()).thenReturn(List.of(postulation));
                mapper.when(() -> PostulationMapper.toDTO(postulation)).thenReturn(postulationDTO);

                List<PostulationDTO> result = postulationService.getAllPostulations();

                assertThat(result).hasSize(1);
                }
        }

        @Test
        void getAllPostulations_whenEmpty_shouldReturnEmptyList() {
                when(postulationRepository.findAll()).thenReturn(List.of());

                List<PostulationDTO> result = postulationService.getAllPostulations();

                assertThat(result).isEmpty();
        }

        @Test
        void deletePostulation_shouldCallDeleteById() {
                doNothing().when(postulationRepository).deleteById(1L);

                postulationService.deletePostulation(1L);

                verify(postulationRepository).deleteById(1L);
        }

        @Test
        void getPostulationsByEmployerId_shouldReturnList() {
                try (MockedStatic<PostulationMapper> mapper = mockStatic(PostulationMapper.class)) {
                when(postulationRepository.findByOffer_Employer_Id(1L)).thenReturn(List.of(postulation));
                mapper.when(() -> PostulationMapper.toDTO(postulation)).thenReturn(postulationDTO);

                List<PostulationDTO> result = postulationService.getPostulationsByEmployerId(1L);

                assertThat(result).hasSize(1);
                }
        }

        @Test
        void getPostulationsByEmployerId_whenNone_shouldReturnEmptyList() {
                when(postulationRepository.findByOffer_Employer_Id(99L)).thenReturn(List.of());

                List<PostulationDTO> result = postulationService.getPostulationsByEmployerId(99L);

                assertThat(result).isEmpty();
        }
}