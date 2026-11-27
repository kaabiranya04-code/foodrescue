package com.foodrescue.fooddonation.controller;

import com.foodrescue.fooddonation.dto.CreateFoodBatchRequest;
import com.foodrescue.fooddonation.dto.FoodBatchResponse;
import com.foodrescue.fooddonation.model.BatchStatus;
import com.foodrescue.fooddonation.service.FoodBatchService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/food-batches")
@Tag(name = "Lots de nourriture")
public class FoodBatchController {

    private final FoodBatchService service;

    public FoodBatchController(FoodBatchService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Declarer un lot de nourriture invendue")
    public ResponseEntity<FoodBatchResponse> create(@Valid @RequestBody CreateFoodBatchRequest request) {
        FoodBatchResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/food-batches/" + created.id())).body(created);
    }

    @GetMapping
    @Operation(summary = "Lister les lots (filtre optionnel par statut)")
    public List<FoodBatchResponse> list(@RequestParam(required = false) BatchStatus status) {
        return service.list(status);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un lot")
    public FoodBatchResponse get(@PathVariable String id) {
        return service.get(id);
    }
}
