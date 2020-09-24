package no.difi.altinn;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static junit.framework.TestCase.*;

@SpringBootTest
@ActiveProfiles("test")
public class RightControllerTest {

    static WireMockServer wireMockServer;
    @Autowired
    private RightController rightController;

    @BeforeAll
    static void setup() {
        wireMockServer = new WireMockServer(wireMockConfig().port(9991)); //No-args constructor will start on port 8080, no HTTPS
        wireMockServer.start();
    }

    @AfterAll
    static void tearDown() {
        wireMockServer.stop();
    }

    @Test
    public void getRoles() {
        String ssn = "30045002388";
        String orgNumber = "910027913";
        String testUrl = "/altinn-api-mock/rights?subject="+ssn+"&reportee="+orgNumber+"&ForceEIAuthentication=";

        configureFor("localhost", 9991);
        stubFor(get(urlEqualTo(testUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("rights-test.json")));

        ResponseEntity<RightResponse> roleList = rightController.getRoles(ssn, orgNumber);

        RightResponse body = roleList.getBody();

        assertNotNull(body);
        assertEquals(ssn, body.getPersonIdentificator());
        assertEquals(orgNumber, body.getOrganizationNumber());
        Set<String> serviceCodes = new HashSet<>(Arrays.asList("5455", "5456"));
        assertEquals(serviceCodes, body.getServiceCodes());

        verify(getRequestedFor(urlEqualTo(testUrl)));
    }

    @Test
    public void getRolesWithOData() {
        String ssn = "30045002388";
        String orgNumber = "910027913";
        String serviceCode1 = "5455";
        String serviceCode2 = "5456";
        String testUrl = "/altinn-api-mock/rights?subject="+ssn+"&reportee="+orgNumber+
                "&ForceEIAuthentication=$filter=ServiceCode+eq+%27" + serviceCode1 +
                "%27+or+ServiceCode+eq+%27" + serviceCode2 + "%27";

        configureFor("localhost", 9991);
        stubFor(get(urlEqualTo(testUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("rights-test.json")));

        String[] serviceCodes = new String[]{serviceCode1, serviceCode2};
        ResponseEntity<RightResponse> roleList = rightController.getRolesWithServiceCodes(ssn, orgNumber, serviceCodes);

        RightResponse body = roleList.getBody();

        assertNotNull(body);
        assertEquals(ssn, body.getPersonIdentificator());
        assertEquals(orgNumber, body.getOrganizationNumber());
        assertEquals(new HashSet<>(Arrays.asList(serviceCodes)), body.getServiceCodes());

        verify(getRequestedFor(urlEqualTo(testUrl)));
    }

    @Test
    public void testMock() {
        ResponseEntity<RightResponse> roles = rightController.getRoles("06045000883", "910027913");
        assertNotNull(roles.getBody());
        assertEquals(2, roles.getBody().getServiceCodes().size());
        assertTrue(roles.getBody().getServiceCodes().contains("1234"));
        assertTrue(roles.getBody().getServiceCodes().contains("2456"));
    }

}