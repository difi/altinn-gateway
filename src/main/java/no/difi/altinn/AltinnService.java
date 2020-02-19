package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.domain.Delegation;
import no.difi.altinn.domain.RightResource;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = {"delegations"})
public class AltinnService {
    private final AltinnClient client;

    public AltinnService(AltinnClient client) {
        this.client = client;
    }

    @Cacheable
    public List<Delegation> getDelegations(String scope, String consumerOrg, String supplierOrg) {
        return client.getDelegations(getUrlWithParameters(scope, consumerOrg, supplierOrg), false);
    }

    public RightResource getRights(String subject, String reportee){
        return client.getRights(getRightsUri(subject, reportee));
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

    URI getRightsUri(String subject, String reportee){
        UriComponentsBuilder builder = client.getAltinnAuthorizationURIBuilder()
                .pathSegment("rights")
                .queryParam("subject", subject)
                .queryParam("reportee", reportee)
                .queryParam("ForceEIAuthentication", "");
        return builder.buildAndExpand().toUri();
    }
}
