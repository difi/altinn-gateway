package no.difi.altinn.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RightResource {

    @JsonProperty("Subject")
    private Subject subject;

    @JsonProperty("Reportee")
    private Reportee reportee;

    @JsonProperty("Rights")
    private List<Right> rights;
}
