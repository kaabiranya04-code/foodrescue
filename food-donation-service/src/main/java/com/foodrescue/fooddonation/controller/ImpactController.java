package com.foodrescue.fooddonation.controller;

import com.foodrescue.fooddonation.dto.ImpactResponse;
import com.foodrescue.fooddonation.service.ImpactService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/impact")
@Tag(name = "Impact")
public class ImpactController {

    private final ImpactService service;

    public ImpactController(ImpactService service) {
        this.service = service;
    }

    @GetMapping
    @Operation(summary = "Impact cumule : kg sauves, repas, CO2 evite (estimations configurables)")
    public ImpactResponse impact() {
        return service.compute();
    }
}
