package com.jobcol.backend.PostulationService.service;

import java.util.List;
import java.util.Optional;

import com.jobcol.backend.shared.dto.PostulationDTO;

public interface PostulationService {
    PostulationDTO createPostulation(PostulationDTO postulationDTO);

    PostulationDTO updateStatus(Long id, String status);

    Optional<PostulationDTO> getPostulationById(Long id);

    List<PostulationDTO> getPostulationsByUserId(Long userId);

    List<PostulationDTO> getPostulationsByJobOfferId(Long jobOfferId);

    List<PostulationDTO> getAllPostulations();

    void deletePostulation(Long id);

    List<PostulationDTO> getPostulationsByEmployerId(Long employerId);
} 