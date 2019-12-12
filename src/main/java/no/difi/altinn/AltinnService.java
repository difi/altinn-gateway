package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.domain.Delegation;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.List;

@Service
@Slf4j
public class AltinnService {
    private final AltinnClient client;

    public AltinnService(AltinnClient client) {
        this.client = client;
    }

    public List<Delegation> getDelegations(String scope, String consumerOrg, String supplierOrg) {
        return client.getDelegation(getUrlWhenEscapingDoesntWork(scope, consumerOrg, supplierOrg));
    }

    //TODO: Ryddigere å bruke URIBuilder og escape spesialtegn, når Altinn støtter det
    private URIBuilder getUrlWithParameters(String scope, String consumerOrg, String supplierOrg) {
        URIBuilder uriBuilder = client.getAltinnURIBuilder()
                .setPath("delegations")
                .setParameter("scope", scope);
        if (consumerOrg != null) {
            uriBuilder.setParameter("consumerOrg", consumerOrg);
        }
        if (supplierOrg != null) {
            uriBuilder.setParameter("supplierOrg", supplierOrg);
        }
        return uriBuilder;
    }

    private String getUrlWhenEscapingDoesntWork(String scope, String consumerOrg, String supplierOrg) {
        try {
            String url = client.getAltinnURIBuilder().build().toString();
            url += "delegations?scope=" + scope;
            if (consumerOrg != null) {
                url += "&consumerOrg=" + consumerOrg;
            }
            if (supplierOrg != null) {
                url += "&supplierOrg=" + supplierOrg;
            }
            return url;
        } catch (URISyntaxException e) {
            log.error("Uri was not valid " + e.getMessage());
            throw new RuntimeException(e);
        }
    }


}
