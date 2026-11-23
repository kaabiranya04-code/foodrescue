package com.foodrescue.charity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.foodrescue.charity.exception.CharityNotApprovedException;
import com.foodrescue.charity.exception.CharityNotFoundException;
import com.foodrescue.charity.exception.RegistrationNotFoundException;
import com.foodrescue.charity.model.Charity;
import com.foodrescue.charity.model.DonationRegistration;
import com.foodrescue.charity.repository.CharityRepository;
import com.foodrescue.charity.repository.RegistrationRepository;
import com.foodrescue.charity.service.CharityService;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/** Tests unitaires de la logique métier (sans SOAP ni base de données). */
@ExtendWith(MockitoExtension.class)
class CharityServiceTest {

    @Mock
    private CharityRepository charities;

    @Mock
    private RegistrationRepository registrations;

    @InjectMocks
    private CharityService service;

    private DonationRegistration sampleRegistration() {
        return new DonationRegistration("reg-1", "D-1", "CH-0001", "El Baraka", "Khobz",
                new BigDecimal("20"), LocalDate.of(2026, 11, 21), Instant.now());
    }

    @Test
    void verify_unknownCharity_throws() {
        when(charities.findById("CH-9999")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.verify("CH-9999"))
                .isInstanceOf(CharityNotFoundException.class);
    }

    @Test
    void register_notApprovedCharity_throws() {
        when(registrations.findByDonationId("D-1")).thenReturn(Optional.empty());
        when(charities.findById("CH-0003"))
                .thenReturn(Optional.of(new Charity("CH-0003", "X", "Sousse", false)));
        assertThatThrownBy(() -> service.register("D-1", "CH-0003", "El Baraka", "Khobz",
                new BigDecimal("5"), LocalDate.now()))
                .isInstanceOf(CharityNotApprovedException.class);
    }

    @Test
    void register_approvedCharity_savesRegistration() {
        when(registrations.findByDonationId("D-1")).thenReturn(Optional.empty());
        when(charities.findById("CH-0001"))
                .thenReturn(Optional.of(new Charity("CH-0001", "Amal", "Tunis", true)));
        when(registrations.save(any(DonationRegistration.class))).thenAnswer(inv -> inv.getArgument(0));

        DonationRegistration result = service.register("D-1", "CH-0001", "El Baraka", "Khobz",
                new BigDecimal("20"), LocalDate.of(2026, 11, 21));

        assertThat(result.getStatus()).isEqualTo(DonationRegistration.REGISTERED);
        assertThat(result.getRegistrationId()).isNotBlank();
    }

    @Test
    void register_sameDonationTwice_returnsExistingRegistration() {
        DonationRegistration existing = sampleRegistration();
        when(registrations.findByDonationId("D-1")).thenReturn(Optional.of(existing));

        DonationRegistration result = service.register("D-1", "CH-0001", "El Baraka", "Khobz",
                new BigDecimal("20"), LocalDate.now());

        assertThat(result).isSameAs(existing);
    }

    @Test
    void issueReceipt_unknownRegistration_throws() {
        when(registrations.findById("nope")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.issueReceipt("nope"))
                .isInstanceOf(RegistrationNotFoundException.class);
    }

    @Test
    void issueReceipt_calledTwice_returnsSameReceiptNumber() {
        DonationRegistration registration = sampleRegistration();
        when(registrations.findById("reg-1")).thenReturn(Optional.of(registration));
        when(registrations.save(any(DonationRegistration.class))).thenAnswer(inv -> inv.getArgument(0));

        String first = service.issueReceipt("reg-1").getReceiptNumber();
        String second = service.issueReceipt("reg-1").getReceiptNumber();

        assertThat(first).startsWith("REC-");
        assertThat(second).isEqualTo(first);
        assertThat(registration.getStatus()).isEqualTo(DonationRegistration.RECEIPT_ISSUED);
    }
}
