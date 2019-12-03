package no.difi.altinn;

import no.difi.altinn.domain.Delegation;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AltinnService {
    private final AltinnClient client;

    public AltinnService(AltinnClient client) {
        this.client = client;
    }

    public List<Delegation> getDelegations(String scope, String consumerOrg, String supplierOrg) {
        URIBuilder uriBuilder = client.getDelegationsURIBuilder()
                .setParameter("scope", scope);
        if (consumerOrg != null) {
            uriBuilder.setParameter("consumer_org", consumerOrg);
        }
        if (supplierOrg != null) {
            uriBuilder.setParameter("supplier_org", supplierOrg);
        }
        return client.getDelegation(uriBuilder.toString());
    }
}
