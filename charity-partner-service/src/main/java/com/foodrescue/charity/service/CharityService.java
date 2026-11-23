package com.foodrescue.charity.service;

import com.foodrescue.charity.exception.CharityNotApprovedException;
import com.foodrescue.charity.exception.CharityNotFoundException;
import com.foodrescue.charity.exception.RegistrationNotFoundException;
import com.foodrescue.charity.model.Charity;
import com.foodrescue.charity.model.DonationRegistration;
import com.foodrescue.charity.repository.CharityRepository;
import com.foodrescue.charity.repository.RegistrationRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Year;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/** Logique métier du partenaire (indépendante de SOAP, donc testable). */
@Service
public class CharityService {

    private final CharityRepository charities;
    private final RegistrationRepository registrations;

    public CharityService(CharityRepository charities, RegistrationRepository registrations) {
        this.charities = charities;
        this.registrations = registrations;
    }

    public Charity verify(String charityId) {
        return charities.findById(charityId)
                .orElseThrow(() -> new CharityNotFoundException(charityId));
    }

    /** Idempotent : un même donationId renvoie toujours le même enregistrement. */
    @Transactional
    public DonationRegistration register(String donationId, String charityId, String restaurantName,
                                         String foodType, BigDecimal quantityKg, LocalDate donationDate) {
        return registrations.findByDonationId(donationId).orElseGet(() -> {
            Charity charity = verify(charityId);
            if (!charity.isApproved()) {
                throw new CharityNotApprovedException(charityId);
            }
            DonationRegistration registration = new DonationRegistration(
                    UUID.randomUUID().toString(), donationId, charityId,
                    restaurantName, foodType, quantityKg, donationDate, Instant.now());
            return registrations.save(registration);
        });
    }

    /** Idempotent : un reçu déjà émis est renvoyé tel quel. */
    @Transactional
    public DonationRegistration issueReceipt(String registrationId) {
        DonationRegistration registration = registrations.findById(registrationId)
                .orElseThrow(() -> new RegistrationNotFoundException(registrationId));
        if (registration.getReceiptNumber() == null) {
            registration.issueReceipt(newReceiptNumber(), Instant.now());
            registrations.save(registration);
        }
        return registration;
    }

    public String charityName(String charityId) {
        return charities.findById(charityId).map(Charity::getName).orElse(charityId);
    }

    private String newReceiptNumber() {
        return "REC-" + Year.now().getValue() + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
