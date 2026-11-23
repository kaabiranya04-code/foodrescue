package com.foodrescue.charity;

import static org.springframework.ws.test.server.RequestCreators.withPayload;
import static org.springframework.ws.test.server.ResponseMatchers.clientOrSenderFault;
import static org.springframework.ws.test.server.ResponseMatchers.noFault;
import static org.springframework.ws.test.server.ResponseMatchers.xpath;

import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.ws.test.server.MockWebServiceClient;
import org.springframework.xml.transform.StringSource;

/** Test d'intégration SOAP : vrais messages XML, vrai endpoint, base H2 en mémoire. */
@SpringBootTest
class CharitySoapIntegrationTest {

    private static final String NS = "http://foodrescue.com/charity";
    private static final Map<String, String> NS_MAP = Map.of("c", NS);

    @Autowired
    private ApplicationContext context;

    private MockWebServiceClient client;

    @BeforeEach
    void setUp() {
        client = MockWebServiceClient.createClient(context);
    }

    private static String verifyRequest(String charityId) {
        return "<c:VerifyCharityRequest xmlns:c=\"" + NS + "\">"
                + "<c:charityId>" + charityId + "</c:charityId>"
                + "</c:VerifyCharityRequest>";
    }

    private static String registerRequest(String donationId, String charityId) {
        return "<c:RegisterDonationRequest xmlns:c=\"" + NS + "\">"
                + "<c:donationId>" + donationId + "</c:donationId>"
                + "<c:charityId>" + charityId + "</c:charityId>"
                + "<c:restaurantName>El Baraka</c:restaurantName>"
                + "<c:foodType>Khobz</c:foodType>"
                + "<c:quantityKg>20</c:quantityKg>"
                + "<c:donationDate>2026-11-21</c:donationDate>"
                + "</c:RegisterDonationRequest>";
    }

    @Test
    void verifyCharity_approved() throws Exception {
        client.sendRequest(withPayload(new StringSource(verifyRequest("CH-0001"))))
                .andExpect(noFault())
                .andExpect(xpath("/c:VerifyCharityResponse/c:approved", NS_MAP).evaluatesTo(true));
    }

    @Test
    void verifyCharity_unknown_returnsSoapFault() throws Exception {
        client.sendRequest(withPayload(new StringSource(verifyRequest("CH-9999"))))
                .andExpect(clientOrSenderFault());
    }

    @Test
    void verifyCharity_invalidFormat_isRejectedByXsd() throws Exception {
        client.sendRequest(withPayload(new StringSource(verifyRequest("ABC"))))
                .andExpect(clientOrSenderFault());
    }

    @Test
    void registerDonation_approvedCharity_isRegistered() throws Exception {
        client.sendRequest(withPayload(new StringSource(registerRequest("D-IT-1", "CH-0001"))))
                .andExpect(noFault())
                .andExpect(xpath("/c:RegisterDonationResponse/c:status", NS_MAP).evaluatesTo("REGISTERED"));
    }

    @Test
    void registerDonation_notApprovedCharity_returnsSoapFault() throws Exception {
        client.sendRequest(withPayload(new StringSource(registerRequest("D-IT-2", "CH-0003"))))
                .andExpect(clientOrSenderFault());
    }
}
