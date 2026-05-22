package com.jobcol.backend.PostulationService.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.jobcol.backend.PostulationService.model.Postulation;

public interface PostulationRepository extends JpaRepository<Postulation, Long> {

    List<Postulation> findByWorker_Id(Long workerId);

    List<Postulation> findByOffer_Id(Long offerId);

    List<Postulation> findAllByOffer_Id(Long offerId);
    
    List<Postulation> findByOffer_Employer_Id(Long employerId);
}
