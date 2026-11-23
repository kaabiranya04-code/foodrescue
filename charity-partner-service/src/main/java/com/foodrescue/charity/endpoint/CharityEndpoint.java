package com.foodrescue.charity.endpoint;

import com.foodrescue.charity.config.WebServiceConfig;
import com.foodrescue.charity.generated.IssueReceiptRequest;
import com.foodrescue.charity.generated.IssueReceiptResponse;
import com.foodrescue.charity.generated.RegisterDonationRequest;
import com.foodrescue.charity.generated.RegisterDonationResponse;
import com.foodrescue.charity.generated.RegistrationStatus;
import com.foodrescue.charity.generated.VerifyCharityRequest;
import com.foodrescue.charity.generated.VerifyCharityResponse;
import com.foodrescue.charity.model.Charity;
import com.foodrescue.charity.model.DonationRegistration;
import com.foodrescue.charity.service.CharityService;
import com.foodrescue.charity.util.XmlDates;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/** Couche SOAP : traduit les messages XML en appels au service métier. */
@Endpoint
public class CharityEndpoint {

    private static final String NS = WebServiceConfig.NAMESPACE;

    private final CharityService service;

    public CharityEndpoint(CharityService service) {
        this.service = service;
    }

    @PayloadRoot(namespace = NS, localPart = "VerifyCharityRequest")
    @ResponsePayload
    public VerifyCharityResponse verifyCharity(@RequestPayload VerifyCharityRequest request) {
        Charity charity = service.verify(request.getCharityId());
        VerifyCharityResponse response = new VerifyCharityResponse();
        response.setCharityId(charity.getCharityId());
        response.setName(charity.getName());
        response.setRegion(charity.getRegion());
        response.setApproved(charity.isApproved());
        return response;
    }

    @PayloadRoot(namespace = NS, localPart = "RegisterDonationRequest")
    @ResponsePayload
    public RegisterDonationResponse registerDonation(@RequestPayload RegisterDonationRequest request) {
        DonationRegistration registration = service.register(
                request.getDonationId(),
                request.getCharityId(),
                request.getRestaurantName(),
                request.getFoodType(),
                request.getQuantityKg(),
                XmlDates.toLocalDate(request.getDonationDate()));
        RegisterDonationResponse response = new RegisterDonationResponse();
        response.setRegistrationId(registration.getRegistrationId());
        response.setStatus(RegistrationStatus.fromValue(registration.getStatus()));
        response.setRegisteredAt(XmlDates.toXml(registration.getRegisteredAt()));
        return response;
    }

    @PayloadRoot(namespace = NS, localPart = "IssueReceiptRequest")
    @ResponsePayload
    public IssueReceiptResponse issueReceipt(@RequestPayload IssueReceiptRequest request) {
        DonationRegistration registration = service.issueReceipt(request.getRegistrationId());
        IssueReceiptResponse response = new IssueReceiptResponse();
        response.setReceiptNumber(registration.getReceiptNumber());
        response.setRegistrationId(registration.getRegistrationId());
        response.setCharityName(service.charityName(registration.getCharityId()));
        response.setQuantityKg(registration.getQuantityKg());
        response.setIssuedAt(XmlDates.toXml(registration.getReceiptIssuedAt()));
        return response;
    }
}
