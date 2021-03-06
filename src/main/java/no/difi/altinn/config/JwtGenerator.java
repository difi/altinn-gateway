package no.difi.altinn.config;

import com.google.common.base.Strings;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.security.KeyProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
public class JwtGenerator {
    private JwkProperties properties;

    private RestTemplate restTemplate;

    private String currentAccessToken;

    private KeyProvider keyProvider;

    public JwtGenerator(JwkProperties properties, RestTemplate restTemplate, KeyProvider keyProvider) {
        this.properties = properties;
        this.restTemplate = restTemplate;
        this.keyProvider = keyProvider;
    }

    public String acquireAccessToken() {
        if (currentAccessToken != null) {
            return currentAccessToken;
        }
        return acquireNewAccessToken();
    }

    public String acquireNewAccessToken() {
        String jwt = makeJwt();
        TokenResponse tokenResponse = callTokenEndpoint(properties.getTokenEndpoint(), jwt);
        currentAccessToken = tokenResponse.getAccessToken();
        return currentAccessToken;
    }

    private String makeJwt() {
        try {
            JWSHeader jwtHeader;
            if (Strings.isNullOrEmpty(properties.getKid())) {
                List<Base64> certChain = new ArrayList<>();
                certChain.add(Base64.encode(keyProvider.certificate().getEncoded()));
                jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).x509CertChain(certChain).build();
            }else {
                jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(properties.getKid()).build();
            }
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .audience(properties.getAud())
                    .issuer(properties.getIss())
                    .claim("resource", properties.getResource())
                    .claim("scope", properties.getScope())
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(new Date(Clock.systemUTC().millis()))
                    .expirationTime(new Date(Clock.systemUTC().millis() + 120000)) // Expiration time is 120 sec.
                    .build();
            JWSSigner signer = new RSASSASigner(keyProvider.privateKey());
            SignedJWT signedJWT = new SignedJWT(jwtHeader, claims);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("Couldn't create JWT " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private TokenResponse callTokenEndpoint(String tokenEndpoint, String assertion)  {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "urn:ietf:params:oauth:grant-type:jwt-bearer");
        map.add("assertion", assertion);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        return restTemplate.postForObject(tokenEndpoint, request, TokenResponse.class);
    }
}
