package com.jobcol.backend.OfferService.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.jobcol.backend.OfferService.service.OfferService;
import com.jobcol.backend.shared.dto.OfferDTO;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/offers")
@RequiredArgsConstructor
public class OfferController {

    private final OfferService offerService;

    @GetMapping
    public ResponseEntity<List<OfferDTO>> getAllOffers() {
        return ResponseEntity.ok(offerService.getAllOffers());
    }

    @GetMapping("/active")
    public ResponseEntity<List<OfferDTO>> getActiveOffers() {
        return ResponseEntity.ok(offerService.getActiveOffers());
    }

    @GetMapping("/{id}")
    public ResponseEntity<OfferDTO> getOfferById(@PathVariable Long id) {

        return offerService.getOfferById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/employer/{userId}")
    public ResponseEntity<List<OfferDTO>> getOffersByUserId(@PathVariable Long userId) {

        return ResponseEntity.ok(offerService.getOffersByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<OfferDTO> createOffer(@RequestBody OfferDTO offerDTO) {

        OfferDTO createdOffer = offerService.createOffer(offerDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(createdOffer);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OfferDTO> updateOffer(
            @PathVariable Long id,
            @RequestBody OfferDTO offerDTO) {

        OfferDTO updatedOffer = offerService.updateOffer(id, offerDTO);

        return ResponseEntity.ok(updatedOffer);
    }

    @PatchMapping("/{id}/close")
    public ResponseEntity<OfferDTO> closeOffer(@PathVariable Long id) {

        OfferDTO closedOffer = offerService.closeOffer(id);

        return ResponseEntity.ok(closedOffer);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOffer(@PathVariable Long id) {

        offerService.deleteOffer(id);

        return ResponseEntity.noContent().build();
    }
}