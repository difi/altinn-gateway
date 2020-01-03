package no.difi.altinn.config;

import lombok.Data;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Getter
@Validated
@ConfigurationProperties("maskinporten")
public class JwkProperties {
    @NotNull
    private String iss;

    @NotNull
    private String aud;

    @NotNull
    private String tokenEndpoint;

    @NotNull
    private String scope;

    private String resource;

    private String kid;

}
