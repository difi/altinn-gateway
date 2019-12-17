package no.difi.altinn;

import no.difi.altinn.domain.Delegation;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@SpringBootTest
public class AltinnServiceTest {

    @Mock
    private AltinnClient altinnClient;

    private static final String mockServiceEndpoint = "https://at23.altinn.cloud/maskinporten-api";

    @Before
    public void setup() {
        when(altinnClient.getAltinnURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockServiceEndpoint));
    }

    @Test
    void getDelegations() throws URISyntaxException {
        AltinnService altinnService = new AltinnService(altinnClient);
        when(altinnClient.getAltinnURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockServiceEndpoint));
        String scope = "difi:myScope";
        String[] scopes = new String[]{scope};
        URI expectedUrl = new URI(mockServiceEndpoint + "/delegations?scope=difi:myScope");
        Delegation delegation = Delegation.builder().scopes(scopes).build();
        when(altinnClient.getDelegations(expectedUrl, false)).thenReturn(Collections.singletonList(delegation));
        altinnService.getDelegations(scope, null, null);
        verify(altinnClient, times(1)).getDelegations(expectedUrl, false);
    }

    @Test
    void testCache() throws URISyntaxException {
        AltinnService altinnService = new AltinnService(altinnClient);
        when(altinnClient.getAltinnURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockServiceEndpoint));
        String scope = "difi:myScope";
        String[] scopes = new String[]{scope};
        URI expectedUrl = new URI(mockServiceEndpoint + "/delegations?scope=difi:myScope&consumerOrg=1234&supplierOrg=5678");
        Delegation delegation = Delegation.builder().scopes(scopes).build();
        when(altinnClient.getDelegations(expectedUrl, false)).thenReturn(Collections.singletonList(delegation));
        altinnService.getDelegations(scope, "1234", "5678");
        altinnService.getDelegations(scope, "1234", "5678");
        verify(altinnClient, times(1)).getDelegations(expectedUrl, false);
    }

    @Test
    void testUrlBuilderMethod() {
        AltinnService altinnService = new AltinnService(altinnClient);
        when(altinnClient.getAltinnURIBuilder()).thenReturn(UriComponentsBuilder.fromUriString(mockServiceEndpoint));
        URI url = altinnService.getUrlWithParameters("difi:myScope", "1234", "5678");
        assertEquals(mockServiceEndpoint + "/delegations?scope=difi:myScope&consumerOrg=1234&supplierOrg=5678", url.toString());
    }
}