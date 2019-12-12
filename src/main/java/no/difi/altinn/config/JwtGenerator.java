package no.difi.altinn.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.util.Base64;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.security.KeyStoreProvider;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.security.*;
import java.security.cert.Certificate;
import java.time.Clock;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Slf4j
public class JwtGenerator {
    private JwkProperties properties;

    private ClientProperties clientProperties;

    private RestTemplate restTemplate;

    public JwtGenerator(JwkProperties properties, ClientProperties clientProperties, RestTemplate restTemplate) {
        this.properties = properties;
        this.clientProperties = clientProperties;
        this.restTemplate = restTemplate;
    }

    public String acquireAccessToken() {
        String jwt = makeJwt();
        TokenResponse tokenResponse = callTokenEndpoint(properties.getTokenEndpoint(), jwt);
        return tokenResponse.getAccessToken();
    }

    private String makeJwt() {
        try {
            List<Base64> certChain = new ArrayList<>();
            certChain.add(Base64.encode(getCertificateFromKeystore().getEncoded()));

            JWSHeader jwtHeader = new JWSHeader.Builder(JWSAlgorithm.RS256).x509CertChain(certChain).build();
            JWTClaimsSet claims = new JWTClaimsSet.Builder()
                    .audience(properties.getAud())
                    .issuer(properties.getIss())
                    .claim("resource", properties.getResource())
                    .claim("scope", properties.getScope())
                    .jwtID(UUID.randomUUID().toString())
                    .issueTime(new Date(Clock.systemUTC().millis()))
                    .expirationTime(new Date(Clock.systemUTC().millis() + 120000)) // Expiration time is 120 sec.
                    .build();

            JWSSigner signer = new RSASSASigner(getPrivateKeyFromKeystore());
            SignedJWT signedJWT = new SignedJWT(jwtHeader, claims);
            signedJWT.sign(signer);

            return signedJWT.serialize();
        } catch (Exception e) {
            log.error("Couldn't create JWT " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Certificate getCertificateFromKeystore() throws KeyStoreException {
        final ClientProperties.KeyStoreProperties keyProperties = clientProperties.getKeystore();
        KeyStore keyStore = KeyStoreProvider.from(keyProperties).getKeyStore();
        return keyStore.getCertificate(clientProperties.getKeystore().getKeyAlias());
    }

    private PrivateKey getPrivateKeyFromKeystore() throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {
        final ClientProperties.KeyStoreProperties keyProperties = clientProperties.getKeystore();
        KeyStore keyStore = KeyStoreProvider.from(keyProperties).getKeyStore();
        return (PrivateKey) keyStore.getKey(clientProperties.getKeystore().getKeyAlias(), clientProperties.getKeystore().getKeyPassword().toCharArray()); // Read from KeyStore
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
