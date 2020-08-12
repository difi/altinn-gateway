package no.difi.altinn.config;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.AltinnDelegationsClient;
import no.difi.altinn.AltinnRightsClient;
import no.difi.altinn.security.KeyProvider;
import no.difi.altinn.security.KeyStoreProvider;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.GeneralSecurityException;
import java.security.KeyStore;


@Slf4j
@Configuration
@EnableConfigurationProperties({ClientProperties.class, JwkProperties.class})
public class ClientConfig {

    @Bean
    public AltinnRightsClient altinnRightsClient(ClientProperties clientProperties, JwtGenerator jwtGenerator) {
        CloseableHttpClient httpClient = getCloseableRightsHttpClient(clientProperties);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return new AltinnRightsClient(clientProperties, restTemplate);
    }

    @Bean
    public AltinnDelegationsClient altinnDelegationsClient(ClientProperties clientProperties, JwtGenerator jwtGenerator) {
        CloseableHttpClient httpClient = getCloseableDelegationsHttpClient(clientProperties);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        return new AltinnDelegationsClient(clientProperties, restTemplate, jwtGenerator);
    }

    @Bean
    public JwtGenerator jwtGenerator(JwkProperties jwkProperties, ClientProperties clientProperties, RestTemplate restTemplate, KeyProvider keyProvider) {
        return new JwtGenerator(jwkProperties, restTemplate, keyProvider);
    }

    @Bean(name="rights-http-client")
    public CloseableHttpClient getCloseableRightsHttpClient(ClientProperties properties) {
        return HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext(properties.getRightsKeystore())))
                .build();
    }

    @Bean(name="delegations-http-client")
    public CloseableHttpClient getCloseableDelegationsHttpClient(ClientProperties properties) {
        return HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext(properties.getDelegationsKeystore())))
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private SSLContext sslContext(ClientProperties.KeyStoreProperties keyProperties) {
        try {
            KeyStore keyStore = KeyStoreProvider.from(keyProperties).getKeyStore();
            return SSLContexts.custom()
                    .loadKeyMaterial(keyStore, keyProperties.getKeyPassword().toCharArray())
                    .build();
        } catch (NullPointerException e) {
            throw new IllegalStateException("Keystore missing.", e);
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Could not load keystore", e);
        }
    }

    @Bean
    public KeyProvider delegationsKeyProvider(ClientProperties properties) {
        final ClientProperties.KeyStoreProperties keyProperties = properties.getDelegationsKeystore();
        KeyStore keyStore = KeyStoreProvider.from(keyProperties).getKeyStore();
        return new KeyProvider(keyStore, keyProperties.getKeyAlias(), keyProperties.getKeyPassword());
    }


}
