package no.difi.altinn;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.difi.altinn.domain.AltinnError;
import no.difi.altinn.domain.Delegation;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.client.HttpClientErrorException;

import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebMvcTest(DelegationController.class)
public class AltinnGwMvcTest {

    @MockBean
    AltinnService altinnService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void getDelegation() throws Exception {
        final String scope = "skatteetaten:tdites";
        final String consumerOrg = "910027913";
        final String supplierOrg = "910029606";
        Delegation delegation = Delegation.builder()
                .scopes(new String[]{scope})
                .consumerOrgnr(consumerOrg)
                .supplierOrgnr(supplierOrg).build();
        List<Delegation> delegationList = Collections.singletonList(delegation);

        given(altinnService.getDelegations(scope, consumerOrg, supplierOrg)).willReturn(delegationList);

        ResultActions result = mvc.perform(get("/delegations")
                .param("scope", scope)
                .param("consumer_org", consumerOrg)
                .param("supplier_org", supplierOrg)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].consumer_org", is(delegation.getConsumerOrgnr())))
                .andExpect(jsonPath("$[0].scopes[0]", is(delegation.getScopes()[0])));
    }

    @Test
    public void inputErrorShouldReturn400WithSameError() throws Exception {
        final String scope = "skatteetaten:tdites";
        final String consumerOrg = "910027913";
        final String supplierOrg = "123";
        AltinnError error = AltinnError.builder()
                .code(40050)
                .description("Reportee 123 not found.")
                .build();
        ObjectMapper mapper = new ObjectMapper();
        HttpClientErrorException errorException = new HttpClientErrorException(HttpStatus.BAD_REQUEST, HttpStatus.BAD_REQUEST.getReasonPhrase(), mapper.writeValueAsString(error).getBytes(), Charset.defaultCharset());
        given(altinnService.getDelegations(scope, consumerOrg, supplierOrg)).willThrow(errorException);

        ResultActions result = mvc.perform(get("/delegations")
                .param("scope", scope)
                .param("consumer_org", consumerOrg)
                .param("supplier_org", supplierOrg)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code", is(error.getCode())))
                .andExpect(jsonPath("$.error_description", is(error.getDescription())));
    }

    @Test
    public void notFoundErrorShouldReturn503() throws Exception {
        final String scope = "skatteetaten:tdites";
        final String consumerOrg = "910027913";
        final String supplierOrg = "910027913";
        ObjectMapper mapper = new ObjectMapper();
        HttpClientErrorException errorException = new HttpClientErrorException(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.getReasonPhrase(), null, Charset.defaultCharset());
        given(altinnService.getDelegations(scope, consumerOrg, supplierOrg)).willThrow(errorException);

        ResultActions result = mvc.perform(get("/delegations")
                .param("scope", scope)
                .param("consumer_org", consumerOrg)
                .param("supplier_org", supplierOrg)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isServiceUnavailable());
    }
}

