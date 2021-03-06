package no.difi.altinn.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.security.KeyStore;

@Data
@Validated
@ConfigurationProperties("altinn")
public class ClientProperties {
    @NotNull
    private String serviceEndpoint;

    @NotNull
    private String authorizationEndpoint;

    @NotNull
    private String apiKey;

    @Valid
    private KeyStoreProperties rightsKeystore;

    @Valid
    private KeyStoreProperties delegationsKeystore;

    @Data
    @ToString(exclude = {"password", "keyPassword"})
    public static class KeyStoreProperties {
        private String type = KeyStore.getDefaultType();
        @NotNull
        private Resource path;
        @NotNull
        private String password;
        private String keyAlias;
        private String keyPassword;
    }


}
