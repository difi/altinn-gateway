package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.config.ClientProperties;
import no.difi.altinn.domain.Delegation;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Slf4j
public class AltinnClient {

    private final ClientProperties properties;
    private final RestTemplate template;

    public AltinnClient(ClientProperties properties, RestTemplate template) {
        this.properties = properties;
        this.template = template;
    }

    public URIBuilder getURIBuilder() {
        return new URIBuilder().setPath(properties.getServiceEndpoint());
    }

    public Delegation getDelegation(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity entity = new HttpEntity<>(headers);

        log.debug("Gjer oppslag på: " + url);


        ResponseEntity<Delegation> delegation = template.exchange(url, HttpMethod.GET, entity, Delegation.class);
        return delegation.getBody();

    }
}
