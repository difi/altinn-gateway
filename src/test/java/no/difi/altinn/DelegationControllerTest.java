package no.difi.altinn;

import com.github.tomakehurst.wiremock.WireMockServer;

import no.difi.altinn.domain.Delegation;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class DelegationControllerTest {

    static WireMockServer wireMockServer;
    @Autowired
    private DelegationController delegationController;

    @BeforeAll
    static void setup() {
        wireMockServer= new WireMockServer(wireMockConfig().port(9991)); //No-args constructor will start on port 8080, no HTTPS
        wireMockServer.start();
    }

    @AfterAll
    static void tearDown(){
        wireMockServer.stop();
    }
    @Test
    public void getOneDelegation() {
        String scope = "testscope";
        String consumerOrg = "930763029";
        String supplierOrg = "967630291";
        String testUrl = "/altinn-mock/delegations?scope=" + scope + "&consumerOrg=" + consumerOrg + "&supplierOrg=" + supplierOrg;
        configureFor("localhost", 9991);
        stubFor(get(urlEqualTo(testUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("delegation-test.json")));
        stubFor(post(urlEqualTo("/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("accesstoken.json")));
        ResponseEntity<List<Delegation>> delegationList = delegationController.getDelegations(scope, consumerOrg, supplierOrg);
        List<Delegation> body = delegationList.getBody();

        assertNotNull(body);
        assertEquals(1, body.size());
        assertEquals(scope, body.get(0).getScopes()[0]);
        assertEquals(consumerOrg, body.get(0).getConsumerOrgnr());
        assertEquals(supplierOrg, body.get(0).getSupplierOrgnr());

        verify(getRequestedFor(urlEqualTo(testUrl)));
    }

    @Test
    public void getSeveralConsumersDelegations()  {
        String scope = "testscope";
        String supplierOrg = "967630291";
        String testUrl = "/altinn-mock/delegations?scope=" + scope + "&supplierOrg=" + supplierOrg;
        configureFor("localhost", 9991);
        stubFor(get(urlEqualTo(testUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("multiple-consumers-delegations-test.json")));
        stubFor(post(urlEqualTo("/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("accesstoken.json")));
        ResponseEntity<List<Delegation>> delegationList = delegationController.getDelegations(scope, null, supplierOrg);
        List<Delegation> body = delegationList.getBody();

        String consumerOrg1 = "940763029";
        String consumerOrg2 = "950763029";

        assertNotNull(body);
        assertEquals(2, body.size());
        assertEquals(scope, body.get(0).getScopes()[0]);
        assertEquals(scope, body.get(1).getScopes()[0]);
        assertEquals(consumerOrg1, body.get(0).getConsumerOrgnr());
        assertEquals(consumerOrg2, body.get(1).getConsumerOrgnr());
        assertEquals(supplierOrg, body.get(0).getSupplierOrgnr());
        assertEquals(supplierOrg, body.get(1).getSupplierOrgnr());

        verify(getRequestedFor(urlEqualTo(testUrl)));
    }

    @Test
    public void getSeveralSuppliersDelegations()  {
        String scope = "testscope";
        String consumerOrg = "940763029";
        String testUrl = "/altinn-mock/delegations?scope=" + scope + "&consumerOrg=" + consumerOrg;
        configureFor("localhost", 9991);
        stubFor(get(urlEqualTo(testUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("multiple-suppliers-delegations-test.json")));
        stubFor(post(urlEqualTo("/token"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("accesstoken.json")));
        ResponseEntity<List<Delegation>> delegationList = delegationController.getDelegations(scope, consumerOrg, null);
        List<Delegation> body = delegationList.getBody();

        String supplierOrg1 = "967630291";
        String supplierOrg2 = "970763029";

        assertNotNull(body);
        assertEquals(2, body.size());
        assertEquals(scope, body.get(0).getScopes()[0]);
        assertEquals(scope, body.get(1).getScopes()[0]);
        assertEquals(consumerOrg, body.get(0).getConsumerOrgnr());
        assertEquals(consumerOrg, body.get(1).getConsumerOrgnr());
        assertEquals(supplierOrg1, body.get(0).getSupplierOrgnr());
        assertEquals(supplierOrg2, body.get(1).getSupplierOrgnr());

        verify(getRequestedFor(urlEqualTo(testUrl)));
    }

}
