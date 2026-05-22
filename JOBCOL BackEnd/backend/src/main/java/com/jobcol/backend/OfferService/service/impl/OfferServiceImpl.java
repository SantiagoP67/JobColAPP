package com.jobcol.backend.OfferService.service.impl;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import com.jobcol.backend.NotificationService.model.EmailTemplateBuilder;
import com.jobcol.backend.NotificationService.model.NotificationType;
import com.jobcol.backend.NotificationService.service.NotificationService;
import com.jobcol.backend.OfferService.model.Offer;
import com.jobcol.backend.OfferService.repository.OfferRepository;
import com.jobcol.backend.OfferService.service.OfferService;
import com.jobcol.backend.UserService.model.EmployerProfile;
import com.jobcol.backend.UserService.model.User;
import com.jobcol.backend.UserService.repository.EmployerProfileRepository;
import com.jobcol.backend.UserService.repository.UserRepository;
import com.jobcol.backend.shared.dto.OfferDTO;
import com.jobcol.backend.shared.mappers.OfferMapper;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OfferServiceImpl implements OfferService {

        private final OfferRepository offerRepository;
        private final UserRepository userRepository;
        private final EmployerProfileRepository employerProfileRepository;
        private final NotificationService notificationService;

        @Override
        public OfferDTO createOffer(OfferDTO offerDTO) {

                User employer = userRepository.findById(offerDTO.employerId())
                        .orElseThrow(() -> new RuntimeException("User not found"));

                Offer offer = OfferMapper.toEntity(offerDTO);

                offer.setPublicationDate(LocalDateTime.now());
                offer.setStatus("OPEN");
                offer.setEmployer(employer);

                Offer savedOffer = offerRepository.save(offer);

                updateEmployerStats(employer.getId());

                List<User> workers = userRepository.findByRole("TRABAJADOR");

                List<Long> workerIds = workers.stream()
                        .map(User::getId)
                        .toList();

                // SOLO GUARDA EN BD
                notificationService.createNotificationsBatch(
                        workerIds,
                        "Nueva oferta disponible",
                        "Se ha publicado una nueva oferta: " + savedOffer.getTitle(),
                        NotificationType.INFO
                );

                // ENVÍO DE EMAIL PERSONALIZADO
                for (User worker : workers) {

                if (worker.getEmail() == null || worker.getEmail().isBlank()) {
                        continue;
                }

                String link =
                        "http://localhost:5173/dashboard?tab=search";

                String html = EmailTemplateBuilder.buildNotificationEmail(
                        worker.getUsername(),

                        "Nueva oferta disponible 🚀",

                        "Se ha publicado una nueva oferta que podría interesarte. "
                                + "Ingresa a JobCol para revisar los detalles y postularte.",

                        "Ver oferta",

                        link,

                        savedOffer.getTitle(),

                        savedOffer.getLocation()
                );

                notificationService.sendEmail(
                        worker.getEmail(),
                        "Nueva oferta publicada en JobCol",
                        html
                );
                }

                return OfferMapper.toDTO(savedOffer);
        }

        @Override
        public OfferDTO updateOffer(Long id, OfferDTO offerDTO) {

                Offer offer = offerRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Offer not found"));

                offer.setTitle(offerDTO.title());
                offer.setDescription(offerDTO.description());
                offer.setSalaryRange(offerDTO.salaryRange());

                return OfferMapper.toDTO(
                        offerRepository.save(offer)
                );
        }

        @Override
        public Optional<OfferDTO> getOfferById(Long id) {
                return offerRepository.findById(id)
                        .map(OfferMapper::toDTO);
        }

        @Override
        public List<OfferDTO> getOffersByUserId(Long userId) {
                return offerRepository.findByEmployer_Id(userId)
                        .stream()
                        .map(OfferMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public List<OfferDTO> getAllOffers() {
                return offerRepository.findAll()
                        .stream()
                        .map(OfferMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public List<OfferDTO> getActiveOffers() {
                return offerRepository.findByStatus("OPEN")
                        .stream()
                        .map(OfferMapper::toDTO)
                        .collect(Collectors.toList());
        }

        @Override
        public void deleteOffer(Long id) {
                offerRepository.deleteById(id);
        }

        @Override
        public OfferDTO closeOffer(Long id) {

                Offer offer = offerRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Offer not found"));

                offer.setStatus("CLOSED");

                return OfferMapper.toDTO(
                        offerRepository.save(offer)
                );
        }

        private void updateEmployerStats(Long employerId) {

                EmployerProfile profile =
                        employerProfileRepository
                                .findByUser_Id(employerId)
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Employer profile not found"
                                        )
                                );

                int totalJobs =
                        offerRepository
                                .findByEmployer_Id(employerId)
                                .size();

                profile.setTotalJobsPosted(totalJobs);

                employerProfileRepository.save(profile);
        }
}