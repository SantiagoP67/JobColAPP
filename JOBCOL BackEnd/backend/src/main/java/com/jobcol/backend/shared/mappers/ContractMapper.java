package com.jobcol.backend.shared.mappers;

import java.util.Set;
import java.util.stream.Collectors;

import com.jobcol.backend.HiringService.model.Contract;
import com.jobcol.backend.ReviewService.model.Review;
import com.jobcol.backend.shared.dto.ContractDTO;

public class ContractMapper {

    // Entity → DTO
    public static ContractDTO toDTO(Contract contract) {

        if (contract == null) {
            return null;
        }

        return new ContractDTO(

                contract.getId(),

                contract.getStartDate(),

                contract.getEndDate(),

                contract.getAgreedAmount(),

                contract.getStatus(),

                contract.getWorkerFinished(),

                contract.getEmployerFinished(),

                PostulationMapper.toDTO(
                        contract.getPostulation()
                )

        );
    }

    // DTO → Entity
    public static Contract toEntity(ContractDTO dto) {

        if (dto == null) {
            return null;
        }

        return Contract.builder()

                .id(dto.id())

                .startDate(dto.startDate())

                .endDate(dto.endDate())

                .agreedAmount(dto.agreedAmount())

                .status(dto.status())

                .workerFinished(dto.workerFinished())

                .employerFinished(dto.employerFinished())

                .build();
    }
}