package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.config.ClientProperties;
import no.difi.altinn.config.JwtGenerator;
import no.difi.altinn.domain.Delegation;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class AltinnClient {

    private final ClientProperties properties;
    private final RestTemplate template;
    private final JwtGenerator jwtGenerator;

    public AltinnClient(ClientProperties properties, RestTemplate template, JwtGenerator jwtGenerator) {
        this.properties = properties;
        this.template = template;
        this.jwtGenerator = jwtGenerator;
    }

    URIBuilder getAltinnURIBuilder() {
        try {
            return new URIBuilder(properties.getServiceEndpoint());
        } catch (URISyntaxException e) {
            log.error("Couldn't create URI " + properties.getServiceEndpoint() + " - " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    List<Delegation> getDelegation(String url) {
        String accessToken = jwtGenerator.acquireAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        HttpEntity<Object> entity = new HttpEntity<>(headers);

        log.debug("Gjer oppslag på: " + url);

        ResponseEntity<Delegation[]> responseEntity = template.exchange(url, HttpMethod.GET, entity, Delegation[].class);
        return Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));

    }
}
