package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.config.ClientProperties;
import no.difi.altinn.domain.RightResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
public class AltinnRightsClient {

    private final ClientProperties properties;
    private final RestTemplate template;

    public AltinnRightsClient(ClientProperties properties, RestTemplate template) {
        this.properties = properties;
        this.template = template;
    }

    UriComponentsBuilder getAltinnAuthorizationURIBuilder() {
        return UriComponentsBuilder.fromUriString(properties.getAuthorizationEndpoint());
    }

    ResponseEntity<RightResource> getRights(URI url){
        HttpHeaders headers = new HttpHeaders();
        headers.set("ApiKey", properties.getApiKey());
        HttpEntity<Object> entity = new HttpEntity<>(headers);
        return template.exchange(url, HttpMethod.GET, entity, RightResource.class);
    }
}
