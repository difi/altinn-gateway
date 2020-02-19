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
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

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
        String ssn = "00000000001";
        String orgNumber = "000000002";
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

}