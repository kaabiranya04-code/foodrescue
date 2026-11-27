package com.foodrescue.fooddonation.service;

import com.foodrescue.fooddonation.dto.CreateFoodBatchRequest;
import com.foodrescue.fooddonation.dto.FoodBatchResponse;
import com.foodrescue.fooddonation.exception.NotFoundException;
import com.foodrescue.fooddonation.model.BatchStatus;
import com.foodrescue.fooddonation.model.FoodBatch;
import com.foodrescue.fooddonation.repository.FoodBatchRepository;
import com.foodrescue.fooddonation.repository.RestaurantRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class FoodBatchService {

    private final FoodBatchRepository batches;
    private final RestaurantRepository restaurants;

    public FoodBatchService(FoodBatchRepository batches, RestaurantRepository restaurants) {
        this.batches = batches;
        this.restaurants = restaurants;
    }

    @Transactional
    public FoodBatchResponse create(CreateFoodBatchRequest request) {
        if (!restaurants.existsById(request.restaurantId())) {
            throw new NotFoundException("Restaurant introuvable : " + request.restaurantId());
        }
        FoodBatch saved = batches.save(new FoodBatch(request.restaurantId(), request.foodType(),
                request.quantityKg(), request.expiresAt()));
        return FoodBatchResponse.from(saved);
    }

    @Transactional(readOnly = true)
    public List<FoodBatchResponse> list(BatchStatus status) {
        List<FoodBatch> found = status == null ? batches.findAll() : batches.findByStatus(status);
        return found.stream().map(FoodBatchResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public FoodBatchResponse get(String id) {
        return batches.findById(id).map(FoodBatchResponse::from)
                .orElseThrow(() -> new NotFoundException("Lot introuvable : " + id));
    }
}
