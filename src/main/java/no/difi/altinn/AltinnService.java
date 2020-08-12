package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.audit.AuditLog;
import no.difi.altinn.domain.Delegation;
import no.difi.altinn.domain.RightResource;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Service
@Slf4j
@CacheConfig(cacheNames = {"delegations"})
public class AltinnService {
    private final AltinnDelegationsClient delegationsClient;
    private final AltinnRightsClient rightsClient;
    private final AuditLog auditLog;

    public AltinnService(AltinnRightsClient rightsClient, AltinnDelegationsClient delegationsClient, AuditLog auditLog) {
        this.rightsClient = rightsClient;
        this.delegationsClient = delegationsClient;
        this.auditLog = auditLog;
    }

    @Cacheable
    public List<Delegation> getDelegations(String scope, String consumerOrg, String supplierOrg) {
        return delegationsClient.getDelegations(getUrlWithParameters(scope, consumerOrg, supplierOrg), false);
    }

    public RightResource getRights(String subject, String reportee){
        ResponseEntity<RightResource> responseEntity = rightsClient.getRights(getRightsUri(subject, reportee));
        if(responseEntity.getStatusCode() == HttpStatus.OK) {
            auditLog.rightLookup(subject, reportee);
        }

        return responseEntity.getBody();
    }

    URI getUrlWithParameters(String scope, String consumerOrg, String supplierOrg) {
        UriComponentsBuilder builder = delegationsClient.getAltinnURIBuilder()
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
        UriComponentsBuilder builder = rightsClient.getAltinnAuthorizationURIBuilder()
                .pathSegment("rights")
                .queryParam("subject", subject)
                .queryParam("reportee", reportee)
                .queryParam("ForceEIAuthentication", "");
        return builder.buildAndExpand().toUri();
    }
}
