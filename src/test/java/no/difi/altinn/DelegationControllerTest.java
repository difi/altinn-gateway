package no.difi.altinn;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.ServeEvent;
import no.difi.altinn.domain.Delegation;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
public class DelegationControllerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(9991); // No-args constructor defaults

    @Autowired
    private DelegationController delegationController;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void getOneDelegation() {
        String scope = "testscope";
        String consumerOrg = "930763029";
        String supplierOrg = "967630291";
        String testUrl = "/altinn-mock/delegations?scope=" + scope + "&consumerOrg=" + consumerOrg + "&supplierOrg=" + supplierOrg;
        stubFor(get(urlEqualTo(testUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("delegation-test.json")));

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

        stubFor(get(urlEqualTo(testUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("multiple-consumers-delegations-test.json")));

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

        stubFor(get(urlEqualTo(testUrl))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBodyFile("multiple-suppliers-delegations-test.json")));

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
