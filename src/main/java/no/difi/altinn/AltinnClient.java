package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.config.ClientProperties;
import no.difi.altinn.config.JwtGenerator;
import no.difi.altinn.domain.Delegation;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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

    UriComponentsBuilder getAltinnURIBuilder() {
        return UriComponentsBuilder.fromUriString(properties.getServiceEndpoint());
    }

    List<Delegation> getDelegations(String url, boolean isRetry) {
        String accessToken;
        accessToken = isRetry ? jwtGenerator.acquireNewAccessToken() : jwtGenerator.acquireAccessToken();
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<Delegation[]> responseEntity = template.exchange(url, HttpMethod.GET, entity, Delegation[].class);
            return Arrays.asList(Objects.requireNonNull(responseEntity.getBody()));
        } catch (HttpClientErrorException e) {
            if (!isRetry && e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return getDelegations(url, true);
            } else {
                throw e;
            }
        }
    }
}
