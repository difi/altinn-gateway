package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.domain.Delegation;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.cache.annotation.CacheResult;
import java.net.URI;
import java.util.List;

@Service
@Slf4j
public class AltinnService {
    private final AltinnClient client;

    public AltinnService(AltinnClient client) {
        this.client = client;
    }

    @CacheResult(cacheName = "delegations")
    public List<Delegation> getDelegations(String scope, String consumerOrg, String supplierOrg) {
        return client.getDelegations(getUrlWithParameters(scope, consumerOrg, supplierOrg), false);
    }

    URI getUrlWithParameters(String scope, String consumerOrg, String supplierOrg) {
        UriComponentsBuilder builder = client.getAltinnURIBuilder()
                .pathSegment("delegations")
                .queryParam("scope", scope);
        if (consumerOrg != null) {
            builder.queryParam("consumerOrg", consumerOrg);
        }
        if (supplierOrg != null) {
            builder.queryParam("supplierOrg", supplierOrg);
        }
        return builder.buildAndExpand().toUri();
    }

}
