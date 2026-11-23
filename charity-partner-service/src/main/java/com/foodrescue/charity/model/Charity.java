package com.foodrescue.charity.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** Association caritative connue du "système hérité" du partenaire. */
@Entity
@Table(name = "charity")
public class Charity {

    @Id
    @Column(length = 10)
    private String charityId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String region;

    @Column(nullable = false)
    private boolean approved;

    protected Charity() {
        // requis par JPA
    }

    public Charity(String charityId, String name, String region, boolean approved) {
        this.charityId = charityId;
        this.name = name;
        this.region = region;
        this.approved = approved;
    }

    public String getCharityId() { return charityId; }
    public String getName() { return name; }
    public String getRegion() { return region; }
    public boolean isApproved() { return approved; }
}
