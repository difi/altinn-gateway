package no.difi.altinn.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Right {

    @JsonProperty("RightID")
    private long rightId;

    @JsonProperty("ServiceCode")
    private String serviceCode;

}
