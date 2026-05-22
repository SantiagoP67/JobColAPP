package com.jobcol.backend.shared.mappers;

import com.jobcol.backend.PostulationService.model.Postulation;
import com.jobcol.backend.shared.dto.PostulationDTO;

public class PostulationMapper {

    public static PostulationDTO toDTO(Postulation postulation) {

        if (postulation == null) {
            return null;
        }

        Long workerId = postulation.getWorker() != null
                ? postulation.getWorker().getId()
                : null;

        Long contractId = postulation.getContract() != null
                ? postulation.getContract().getId()
                : null;

        return new PostulationDTO(

                postulation.getId(),

                postulation.getStatus(),

                postulation.getApplicationDate(),

                workerId,

                postulation.getCalification(),

                OfferMapper.toDTO(
                        postulation.getOffer()
                ),

                contractId
        );
    }

    public static Postulation toEntity(PostulationDTO dto) {

        if (dto == null) {
            return null;
        }

        return Postulation.builder()

                .id(dto.id())

                .status(dto.status())

                .applicationDate(dto.applicationDate())

                .calification(dto.calification())

                .build();
    }
}