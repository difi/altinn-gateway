package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.domain.Delegation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import javax.cache.annotation.CacheResult;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class AltinnService {
    private final AltinnClient client;

    @Value("${altinn.escapeScopenames}")
    private boolean escapeScopename;

    public AltinnService(AltinnClient client) {
        this.client = client;
    }

    @CacheResult(cacheName = "delegations")
    public List<Delegation> getDelegations(String scope, String consumerOrg, String supplierOrg) {
        return client.getDelegations(getUrlWithParameters(scope, consumerOrg, supplierOrg, escapeScopename), false);
    }

    //TODO: Bruk escapeScopename = true når Altinn støtter det
    String getUrlWithParameters(String scope, String consumerOrg, String supplierOrg, boolean escapeScopename) {
        String scopeParam = scope;
        if (escapeScopename) {
            try {
                scopeParam = URLEncoder.encode(scope, StandardCharsets.UTF_8.toString());
            } catch (UnsupportedEncodingException e) {
                log.error("Kunne ikke encode scope " + scope, e);
                scopeParam = scope;
            }
        }
        UriComponentsBuilder builder = client.getAltinnURIBuilder()
                .pathSegment("delegations")
                .queryParam("scope", scopeParam);
        if (consumerOrg != null) {
            builder.queryParam("consumerOrg", consumerOrg);
        }
        if (supplierOrg != null) {
            builder.queryParam("supplierOrg", supplierOrg);
        }
        return builder.buildAndExpand().toString();
    }

}
