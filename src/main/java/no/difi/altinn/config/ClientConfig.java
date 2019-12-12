package no.difi.altinn.config;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.AltinnClient;
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
    public AltinnClient altinnClient(ClientProperties clientProperties, JwkProperties jwkProperties) {
        CloseableHttpClient httpClient = getCloseableHttpClient(clientProperties);
        HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        RestTemplate restTemplate = new RestTemplate(requestFactory);
        JwtGenerator jwtGenerator = jwtGenerator(jwkProperties, clientProperties, restTemplate);
        return new AltinnClient(clientProperties, restTemplate, jwtGenerator);
    }

    @Bean
    public JwtGenerator jwtGenerator(JwkProperties jwkProperties, ClientProperties clientProperties, RestTemplate restTemplate) {
        return new JwtGenerator(jwkProperties, clientProperties, restTemplate);
    }

    @Bean
    public CloseableHttpClient getCloseableHttpClient(ClientProperties properties) {
        return HttpClients.custom()
                .setSSLSocketFactory(new SSLConnectionSocketFactory(sslContext(properties)))
                .build();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    private SSLContext sslContext(ClientProperties properties) {
        try {
            final ClientProperties.KeyStoreProperties keyProperties = properties.getKeystore();
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


}
