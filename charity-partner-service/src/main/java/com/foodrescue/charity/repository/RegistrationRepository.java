package com.foodrescue.charity.repository;

import com.foodrescue.charity.model.DonationRegistration;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<DonationRegistration, String> {

    Optional<DonationRegistration> findByDonationId(String donationId);
}
