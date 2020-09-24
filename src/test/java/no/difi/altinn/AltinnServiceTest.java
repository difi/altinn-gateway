package no.difi.altinn;

import no.difi.altinn.audit.AuditLog;
import no.difi.altinn.domain.*;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AltinnServiceTest {

    @Mock
    private AltinnRightsClient altinnRightsClient;

    @Mock
    private AltinnDelegationsClient altinnDelegationsClient;

    @Mock
    private AuditLog auditLog;

    private static final String mockServiceEndpoint = "https://at23.altinn.cloud/maskinporten-api";
    private static final String mockAuthorizationEndpoint = "https://tt02.altinn.no/api/serviceowner/authorization";

    @Before
    public void setup() {
        when(altinnDelegationsClient.getAltinnURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockServiceEndpoint));
    }

    @Test
    void getDelegations() throws URISyntaxException {
        AltinnService altinnService = new AltinnService(altinnRightsClient, altinnDelegationsClient, auditLog);
        when(altinnDelegationsClient.getAltinnURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockServiceEndpoint));
        String scope = "difi:myScope";
        String[] scopes = new String[]{scope};
        URI expectedUrl = new URI(mockServiceEndpoint + "/delegations?scope=difi:myScope");
        Delegation delegation = Delegation.builder().scopes(scopes).build();
        when(altinnDelegationsClient.getDelegations(expectedUrl, false)).thenReturn(Collections.singletonList(delegation));
        altinnService.getDelegations(scope, null, null);
        verify(altinnDelegationsClient, times(1)).getDelegations(expectedUrl, false);
    }

    @Test
    void testCache() throws URISyntaxException {
        AltinnService altinnService = new AltinnService(altinnRightsClient, altinnDelegationsClient, auditLog);
        when(altinnDelegationsClient.getAltinnURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockServiceEndpoint));
        String scope = "difi:myScope";
        String[] scopes = new String[]{scope};
        URI expectedUrl = new URI(mockServiceEndpoint + "/delegations?scope=difi:myScope&consumerOrg=1234&supplierOrg=5678");
        Delegation delegation = Delegation.builder().scopes(scopes).build();
        when(altinnDelegationsClient.getDelegations(expectedUrl, false)).thenReturn(Collections.singletonList(delegation));
        altinnService.getDelegations(scope, "1234", "5678");
        altinnService.getDelegations(scope, "1234", "5678");
        verify(altinnDelegationsClient, times(1)).getDelegations(expectedUrl, false);
    }

    @Test
    void testUrlBuilderMethod() {
        AltinnService altinnService = new AltinnService(altinnRightsClient, altinnDelegationsClient, auditLog);
        when(altinnDelegationsClient.getAltinnURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockServiceEndpoint));
        URI url = altinnService.getUrlWithParameters("difi:myScope", "1234", "5678");
        assertEquals(mockServiceEndpoint + "/delegations?scope=difi:myScope&consumerOrg=1234&supplierOrg=5678", url.toString());
    }

    @Test
    void testRightsUriMethod() {
        AltinnService altinnService = new AltinnService(altinnRightsClient, altinnDelegationsClient, auditLog);
        when(altinnRightsClient.getAltinnAuthorizationURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockAuthorizationEndpoint));
        URI url = altinnService.getRightsUri("30045002388", "910027913");
        assertEquals(mockAuthorizationEndpoint + "/rights?subject=30045002388&reportee=910027913&ForceEIAuthentication=", url.toString());
    }

    @Test
    void getRights() throws URISyntaxException {
        AltinnService altinnService = new AltinnService(altinnRightsClient, altinnDelegationsClient, auditLog);

        String ssn = "00000000001";
        String orgNumber = "000000002";
        URI expectedUrl = new URI(mockAuthorizationEndpoint + "/rights?subject="+ssn+"&reportee="+orgNumber+"&ForceEIAuthentication=");
        Subject subject = Subject.builder().socialSecurityNumber(ssn).build();
        Reportee reportee = Reportee.builder().organizationNumber(orgNumber).build();
        Right right = Right.builder().rightId(1).serviceCode("5455").build();

        RightResource rightResource = RightResource.builder().subject(subject).reportee(reportee).rights(Collections.singletonList(right)).build();

        when(altinnRightsClient.getRights(expectedUrl)).thenReturn(ResponseEntity.ok().body(rightResource));
        when(altinnRightsClient.getAltinnAuthorizationURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockAuthorizationEndpoint));

        altinnService.getRights(ssn, orgNumber);
        verify(altinnRightsClient, times(1)).getRights(expectedUrl);
        verify(auditLog, times(1)).rightLookup(ssn, orgNumber);

    }

    @Test
    void testGetOdataRightsUri() {
        String ssn = "00000000001";
        String orgNumber = "000000002";
        String[] serviceCodes = new String[]{"5540","5539","5538"};
        when(altinnRightsClient.getAltinnAuthorizationURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockAuthorizationEndpoint));
        AltinnService altinnService = new AltinnService(altinnRightsClient, altinnDelegationsClient, auditLog);

        URI odataRightsUri = altinnService.getOdataRightsUri(ssn, orgNumber, serviceCodes);
        assertEquals("https://tt02.altinn.no/api/serviceowner/authorization/rights?subject=00000000001&reportee=000000002&ForceEIAuthentication=$filter=ServiceCode+eq+%275540%27+or+ServiceCode+eq+%275539%27+or+ServiceCode+eq+%275538%27"
                , odataRightsUri.toString());
    }

}