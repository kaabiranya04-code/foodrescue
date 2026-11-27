package com.foodrescue.fooddonation.service;

import com.foodrescue.fooddonation.dto.CompleteDonationRequest;
import com.foodrescue.fooddonation.dto.CreateDonationRequest;
import com.foodrescue.fooddonation.dto.DonationResponse;
import com.foodrescue.fooddonation.exception.ConflictException;
import com.foodrescue.fooddonation.exception.NotFoundException;
import com.foodrescue.fooddonation.model.BatchStatus;
import com.foodrescue.fooddonation.model.Donation;
import com.foodrescue.fooddonation.model.DonationStatus;
import com.foodrescue.fooddonation.model.FoodBatch;
import com.foodrescue.fooddonation.repository.DonationRepository;
import com.foodrescue.fooddonation.repository.FoodBatchRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DonationService {

    private final DonationRepository donations;
    private final FoodBatchRepository batches;

    public DonationService(DonationRepository donations, FoodBatchRepository batches) {
        this.donations = donations;
        this.batches = batches;
    }

    /** Crée un don pour un lot disponible et réserve ce lot. */
    @Transactional
    public DonationResponse create(CreateDonationRequest request) {
        FoodBatch batch = findBatch(request.batchId());
        if (batch.getStatus() != BatchStatus.AVAILABLE) {
            throw new ConflictException("Le lot " + batch.getId() + " n'est plus disponible ("
                    + batch.getStatus() + ")");
        }
        batch.reserve();
        Donation saved = donations.save(new Donation(batch.getId(), batch.getQuantityKg()));
        return DonationResponse.from(saved);
    }

    /** Termine un don : association confirmée, lot marqué comme donné. */
    @Transactional
    public DonationResponse complete(String donationId, CompleteDonationRequest request) {
        Donation donation = findDonation(donationId);
        if (donation.getStatus() == DonationStatus.COMPLETED) {
            throw new ConflictException("Le don " + donationId + " est deja termine");
        }
        donation.complete(request.charityId(), request.receiptNumber());
        findBatch(donation.getBatchId()).markDonated();
        return DonationResponse.from(donation);
    }

    @Transactional(readOnly = true)
    public List<DonationResponse> list() {
        return donations.findAll().stream().map(DonationResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public DonationResponse get(String id) {
        return DonationResponse.from(findDonation(id));
    }

    private FoodBatch findBatch(String id) {
        return batches.findById(id).orElseThrow(() -> new NotFoundException("Lot introuvable : " + id));
    }

    private Donation findDonation(String id) {
        return donations.findById(id).orElseThrow(() -> new NotFoundException("Don introuvable : " + id));
    }
}
