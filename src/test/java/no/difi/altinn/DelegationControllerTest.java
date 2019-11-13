package no.difi.altinn;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import no.difi.altinn.domain.Delegation;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;

import java.util.List;

import static junit.framework.TestCase.assertNotNull;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@RunWith(SpringJUnit4ClassRunner.class)
public class DelegationControllerTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8099); // No-args constructor defaults

    @Autowired
    private DelegationController delegationController;

    @Autowired
    MockMvc mockMvc;

    @Test
    public void exampleTest() {
//        stubFor(get(urlEqualTo("/my/resource"))
//                .withHeader("Accept", equalTo("text/xml"))
//                .willReturn(aResponse()
//                        .withStatus(200)
//                        .withHeader("Content-Type", "text/xml")
//                        .withBody("<response>Some content</response>")));

        String scope = "scope";
        String consumerOrgnr = "911222333";
        String supplierOrgnr = "922444333";
        ResponseEntity<List<Delegation>> delegation = delegationController.getDelegation(scope, consumerOrgnr, supplierOrgnr);

        assertNotNull(delegation);

//        verify(postRequestedFor(urlMatching("/my/resource/[a-z0-9]+"))
//                .withRequestBody(matching(".*<message>1234</message>.*"))
//                .withHeader("Content-Type", notMatching("application/json")));
    }

    @Test
    public void getDelegation() throws Exception {
        String url = new URIBuilder("/delegation")
                .addParameter("scope", "myScope")
                .addParameter("consumer_org", "myConsumer")
                .addParameter("supplier_org", "mySupplier")
                .build().toString();

        mockMvc.perform(MockMvcRequestBuilders.get(url)
                .contentType("application/x-www-form-urlencoded"))
                .andExpect(status().is2xxSuccessful());


    }
}
