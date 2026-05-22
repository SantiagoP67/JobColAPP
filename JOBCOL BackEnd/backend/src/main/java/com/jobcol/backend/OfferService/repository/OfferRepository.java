package com.jobcol.backend.OfferService.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jobcol.backend.OfferService.model.Offer;

public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByEmployer_Id(Long employerId);

    List<Offer> findByStatus(String string);
    
}
