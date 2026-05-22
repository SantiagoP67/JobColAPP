package com.jobcol.backend.PostulationService.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.jobcol.backend.HiringService.model.Contract;
import com.jobcol.backend.NotificationService.model.EmailTemplateBuilder;
import com.jobcol.backend.NotificationService.model.Notification;
import com.jobcol.backend.NotificationService.model.NotificationType;
import com.jobcol.backend.NotificationService.service.NotificationService;
import com.jobcol.backend.OfferService.model.Offer;
import com.jobcol.backend.OfferService.repository.OfferRepository;
import com.jobcol.backend.PostulationService.model.Postulation;
import com.jobcol.backend.PostulationService.repository.PostulationRepository;
import com.jobcol.backend.PostulationService.service.PostulationService;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.PostulationDTO;
import com.jobcol.backend.shared.mappers.PostulationMapper;

import jakarta.transaction.Transactional;

@Service
@RequiredArgsConstructor
public class PostulationServiceImpl implements PostulationService {

        private final NotificationService notificationService;
        private final PostulationRepository postulationRepository;
        private final OfferRepository offerRepository;
        private final UserRepository userRepository;

        @Override
        @Transactional
        public PostulationDTO createPostulation(PostulationDTO postulationDTO) {

        Long offerId = postulationDTO.offer().id();

        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() ->
                        new RuntimeException("Offer not found"));

        User worker = userRepository.findById(postulationDTO.workerId())
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        Postulation postulation = Postulation.builder()
                .status("PENDING")
                .applicationDate(LocalDateTime.now())
                .calification(postulationDTO.calification())
                .offer(offer)
                .worker(worker)
                .build();

        offer.getPostulations().add(postulation);

        Postulation saved = postulationRepository.save(postulation);

        User employer = offer.getEmployer();

        String emailHtml = EmailTemplateBuilder.buildNotificationEmail(
                employer.getFirstName() + " " + employer.getLastName(),
                "Nueva postulación recibida",
                worker.getFirstName() + " " + worker.getLastName() +
                        " se acaba de postular a tu oferta.",
                "Ver postulaciones",
                "http://localhost:5173/dashboard?tab=candidates",
                offer.getTitle(),
                offer.getLocation()
        );

                notificationService.createNotificationOnly(
                        employer.getId(),
                        "Nueva postulación recibida",
                        worker.getFirstName() + " " + worker.getLastName() + " se ha postulado a tu oferta",
                        NotificationType.POSTULACION_RECIBIDA
                );

                notificationService.sendEmail(
                        employer.getEmail(),
                        "Nueva postulación recibida",
                        emailHtml
                );
                return PostulationMapper.toDTO(saved);
        }

        @Override
        public PostulationDTO updateStatus(Long id, String status) {

                Postulation postulation = postulationRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Postulation not found"));

                postulation.setStatus(status);

                return PostulationMapper.toDTO(postulationRepository.save(postulation));
        }

        @Override
        public Optional<PostulationDTO> getPostulationById(Long id) {
                return postulationRepository.findById(id)
                        .map(PostulationMapper::toDTO);
        }

        @Override
        public List<PostulationDTO> getPostulationsByUserId(Long userId) {
                return postulationRepository.findByWorker_Id(userId)
                        .stream()
                        .map(PostulationMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public List<PostulationDTO> getPostulationsByJobOfferId(Long jobOfferId) {
                return postulationRepository.findByOffer_Id(jobOfferId)
                        .stream()
                        .map(PostulationMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public List<PostulationDTO> getAllPostulations() {
                return postulationRepository.findAll()
                        .stream()
                        .map(PostulationMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public void deletePostulation(Long id) {
                postulationRepository.deleteById(id);
        }

        @Override
        public List<PostulationDTO> getPostulationsByEmployerId(Long employerId) {
                return postulationRepository.findByOffer_Employer_Id(employerId)
                        .stream()
                        .map(PostulationMapper::toDTO)
                        .collect(Collectors.toList());
        }
        
}
