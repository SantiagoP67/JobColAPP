package com.jobcol.backend.OfferService.service;

import java.util.List;
import java.util.Optional;

import com.jobcol.backend.shared.dto.OfferDTO;

public interface OfferService {

    OfferDTO createOffer(OfferDTO offerDTO);

    OfferDTO updateOffer(Long id, OfferDTO offerDTO);

    Optional<OfferDTO> getOfferById(Long id);

    List<OfferDTO> getOffersByUserId(Long userId);

    List<OfferDTO> getAllOffers();

    List<OfferDTO> getActiveOffers();

    void deleteOffer(Long id);

    OfferDTO closeOffer(Long id);
}
