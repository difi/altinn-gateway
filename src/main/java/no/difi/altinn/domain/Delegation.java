package no.difi.altinn.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Delegation {
    @JsonProperty("scopes")
    private String[] scopes;

    @JsonProperty("consumer_org")
    private String consumerOrgnr;

    @JsonProperty("supplier_org")
    private String supplierOrgnr;

    @JsonProperty("delegation_scheme")
    private String delegationScheme;
}
