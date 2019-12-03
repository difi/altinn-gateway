package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.config.ClientProperties;
import no.difi.altinn.domain.Delegation;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class AltinnClient {

    private final ClientProperties properties;
    private final RestTemplate template;

    public AltinnClient(ClientProperties properties, RestTemplate template) {
        this.properties = properties;
        this.template = template;
    }

    URIBuilder getDelegationsURIBuilder() {
        try {
            return new URIBuilder(properties.getDelegationsEndpoint());
        } catch(Exception e) {
            log.error("Couldn't parse delegations endpoint");
            throw new IllegalStateException("Couldn't parse delegations endpoint");
        }
    }

    List<Delegation> getDelegation(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity entity = new HttpEntity<>(headers);

        log.debug("Gjer oppslag på: " + url);

        ResponseEntity<Delegation[]> responseEntity = template.exchange(url, HttpMethod.GET, entity, Delegation[].class);
        return Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));

    }
}
