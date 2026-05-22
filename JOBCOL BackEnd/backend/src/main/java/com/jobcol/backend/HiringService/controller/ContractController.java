package com.jobcol.backend.HiringService.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.jobcol.backend.HiringService.service.ContractService;
import com.jobcol.backend.shared.dto.ContractDTO;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;

@RestController
@RequestMapping("/contracts")
@RequiredArgsConstructor
public class ContractController {
    private final ContractService contractService;

    @PostMapping
    public ResponseEntity<ContractDTO> createContract(@RequestBody ContractDTO contractDTO) {
        ContractDTO createdContract = contractService.createContract(contractDTO);
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(createdContract);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ContractDTO> updateContractStatus(
            @PathVariable Long id,
            @RequestBody String status) {

        ContractDTO updatedContract = contractService.updateStatus(id, status);
        return ResponseEntity.ok(updatedContract);
    }

    @GetMapping
    public ResponseEntity<List<ContractDTO>> getAllContracts() {
        return ResponseEntity.ok(contractService.getAllContracts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDTO> getContractById(@PathVariable Long id) {
        return contractService.getContractById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ContractDTO>> getContractsByUserId(@PathVariable Long userId) {

        List<ContractDTO> contracts = contractService.getContractsByUserId(userId);

        return ResponseEntity.ok(contracts);
    }
    
    @PatchMapping("/{id}/finish")
    public ResponseEntity<ContractDTO> finishContract(@PathVariable Long id) {

        ContractDTO finishedContract = contractService.finishContract(id);

        return ResponseEntity.ok(finishedContract);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Long id) {

        contractService.deleteContract(id);

        return ResponseEntity.noContent().build();
    }
    

    @PutMapping("/{id}/accept")
    public ResponseEntity<ContractDTO> acceptContract(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                contractService.acceptContract(id)
        );
    }

    @PutMapping("/{id}/reject")
    public ResponseEntity<ContractDTO> rejectContract(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                contractService.rejectContract(id)
        );
    }

    @PutMapping("/{id}/finish-request")
    public ResponseEntity<ContractDTO>
    requestFinishContract(@PathVariable Long id,@RequestParam Long userId) {
        return ResponseEntity.ok(
        contractService.requestFinishContract(id,userId)
        );
    }

    @PutMapping("/{id}/confirm-finish")
    public ResponseEntity<ContractDTO>
    confirmFinishContract(
            @PathVariable Long id,
            @RequestParam Long userId
    ) {
        return ResponseEntity.ok(
                contractService.confirmFinishContract(id, userId)
        );
    }
}
