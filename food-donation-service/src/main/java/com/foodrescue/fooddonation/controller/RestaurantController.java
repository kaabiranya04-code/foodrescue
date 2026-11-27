package com.foodrescue.fooddonation.controller;

import com.foodrescue.fooddonation.dto.CreateRestaurantRequest;
import com.foodrescue.fooddonation.dto.RestaurantResponse;
import com.foodrescue.fooddonation.service.RestaurantService;
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
@RequestMapping("/api/v1/restaurants")
@Tag(name = "Restaurants")
public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Enregistrer un restaurant")
    public ResponseEntity<RestaurantResponse> create(@Valid @RequestBody CreateRestaurantRequest request) {
        RestaurantResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/restaurants/" + created.id())).body(created);
    }

    @GetMapping
    @Operation(summary = "Lister les restaurants")
    public List<RestaurantResponse> list() {
        return service.list();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter un restaurant")
    public RestaurantResponse get(@PathVariable String id) {
        return service.get(id);
    }
}
