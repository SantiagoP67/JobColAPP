package com.jobcol.backend.PostulationService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jobcol.backend.PostulationService.service.PostulationService;
import com.jobcol.backend.shared.dto.PostulationDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/postulations")
@RequiredArgsConstructor
public class PostulationController {

    private final PostulationService postulationService;

    @GetMapping
    public ResponseEntity<List<PostulationDTO>> getAllPostulations() {
        return ResponseEntity.ok(postulationService.getAllPostulations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostulationDTO> getPostulationById(@PathVariable Long id) {

        return postulationService.getPostulationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/worker/{userId}")
    public ResponseEntity<List<PostulationDTO>> getPostulationsByUserId(@PathVariable Long userId) {

        return ResponseEntity.ok(postulationService.getPostulationsByUserId(userId));
    }

    @GetMapping("/offer/{offerId}")
    public ResponseEntity<List<PostulationDTO>> getPostulationsByOfferId(@PathVariable Long offerId) {

        return ResponseEntity.ok(postulationService.getPostulationsByJobOfferId(offerId));
    }

    @PostMapping
    public ResponseEntity<PostulationDTO> createPostulation(@RequestBody PostulationDTO postulationDTO) {

        PostulationDTO createdPostulation = postulationService.createPostulation(postulationDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdPostulation);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<PostulationDTO> updateStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        PostulationDTO updatedPostulation = postulationService.updateStatus(id, status);

        return ResponseEntity.ok(updatedPostulation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePostulation(@PathVariable Long id) {

        postulationService.deletePostulation(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/employer/{userId}")
    public ResponseEntity<List<PostulationDTO>> getPostulationsByEmployerId(@PathVariable Long userId) {
        return ResponseEntity.ok(postulationService.getPostulationsByEmployerId(userId));
    }
}
