package com.foodrescue.charity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

/** Don enregistré officiellement auprès de l'association. */
@Entity
@Table(name = "donation_registration")
public class DonationRegistration {

    public static final String REGISTERED = "REGISTERED";
    public static final String RECEIPT_ISSUED = "RECEIPT_ISSUED";

    @Id
    private String registrationId;

    @Column(nullable = false, unique = true)
    private String donationId;

    @Column(nullable = false)
    private String charityId;

    private String restaurantName;
    private String foodType;

    @Column(precision = 10, scale = 2)
    private BigDecimal quantityKg;

    private LocalDate donationDate;

    @Column(nullable = false)
    private String status;

    private Instant registeredAt;
    private String receiptNumber;
    private Instant receiptIssuedAt;

    protected DonationRegistration() {
        // requis par JPA
    }

    public DonationRegistration(String registrationId, String donationId, String charityId,
                                String restaurantName, String foodType, BigDecimal quantityKg,
                                LocalDate donationDate, Instant registeredAt) {
        this.registrationId = registrationId;
        this.donationId = donationId;
        this.charityId = charityId;
        this.restaurantName = restaurantName;
        this.foodType = foodType;
        this.quantityKg = quantityKg;
        this.donationDate = donationDate;
        this.registeredAt = registeredAt;
        this.status = REGISTERED;
    }

    public void issueReceipt(String number, Instant issuedAt) {
        this.receiptNumber = number;
        this.receiptIssuedAt = issuedAt;
        this.status = RECEIPT_ISSUED;
    }

    public String getRegistrationId() { return registrationId; }
    public String getDonationId() { return donationId; }
    public String getCharityId() { return charityId; }
    public String getRestaurantName() { return restaurantName; }
    public String getFoodType() { return foodType; }
    public BigDecimal getQuantityKg() { return quantityKg; }
    public LocalDate getDonationDate() { return donationDate; }
    public String getStatus() { return status; }
    public Instant getRegisteredAt() { return registeredAt; }
    public String getReceiptNumber() { return receiptNumber; }
    public Instant getReceiptIssuedAt() { return receiptIssuedAt; }
}
