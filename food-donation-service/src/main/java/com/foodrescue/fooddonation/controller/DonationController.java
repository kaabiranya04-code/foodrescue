package com.foodrescue.fooddonation.controller;

import com.foodrescue.fooddonation.dto.CompleteDonationRequest;
import com.foodrescue.fooddonation.dto.CreateDonationRequest;
import com.foodrescue.fooddonation.dto.DonationResponse;
import com.foodrescue.fooddonation.service.DonationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/donations")
@Tag(name = "Dons")
public class DonationController {

    private final DonationService service;

    public DonationController(DonationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Creer un don pour un lot disponible (le lot est reserve)")
    public ResponseEntity<DonationResponse> create(@Valid @RequestBody CreateDonationRequest request) {
        DonationResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/donations/" + created.id())).body(created);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Terminer un don (association confirmee). Sera appele automatiquement par l'orchestration.")
    public DonationResponse complete(@PathVariable String id, @Valid @RequestBody CompleteDonationRequest request) {
        return service.complete(id, request);
    }

    @GetMapping
    @Operation(summary = "Lister les dons")
    public List<DonationResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un don")
    public DonationResponse get(@PathVariable String id) {
        return service.get(id);
    }
}
