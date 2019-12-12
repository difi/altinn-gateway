package no.difi.altinn.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Delegation {
    @JsonProperty("scopes")
    private String[] scopes;

    @JsonProperty("consumer_org")
    private String consumerOrgnr;

    @JsonProperty("supplier_org")
    private String supplierOrgnr;

    @JsonProperty("delegation_scheme_Id")
    private String delegationScheme;
}
