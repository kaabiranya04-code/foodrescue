package com.foodrescue.matching.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

/** Historique des matchings (audit). */
@Entity
@Table(name = "match_record")
public class MatchRecord {

    @Id
    private String id;

    private String donationId;
    private String charityId;
    private double score;
    private Instant createdAt;

    protected MatchRecord() {
        // requis par JPA
    }

    public MatchRecord(String donationId, String charityId, double score) {
        this.id = UUID.randomUUID().toString();
        this.donationId = donationId;
        this.charityId = charityId;
        this.score = score;
        this.createdAt = Instant.now();
    }

    public String getId() { return id; }
    public String getDonationId() { return donationId; }
    public String getCharityId() { return charityId; }
    public double getScore() { return score; }
    public Instant getCreatedAt() { return createdAt; }
}
