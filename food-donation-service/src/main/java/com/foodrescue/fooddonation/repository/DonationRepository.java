package com.foodrescue.fooddonation.repository;

import com.foodrescue.fooddonation.model.Donation;
import com.foodrescue.fooddonation.model.DonationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationRepository extends JpaRepository<Donation, String> {

    @Query("select coalesce(sum(d.quantityKg), 0.0) from Donation d where d.status = :status")
    double sumQuantityByStatus(@Param("status") DonationStatus status);

    long countByStatus(DonationStatus status);
}
