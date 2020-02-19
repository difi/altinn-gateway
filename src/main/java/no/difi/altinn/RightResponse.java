package no.difi.altinn;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import no.difi.altinn.domain.Right;
import no.difi.altinn.domain.RightResource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class RightResponse {

    @JsonProperty("person_identificator")
    private String personIdentificator;

    @JsonProperty("organization_number")
    private String organizationNumber;

    @JsonProperty("service_codes")
    private Set<String> serviceCodes;

    public static RightResponse fromRightWrapper(RightResource rightResource){
        return RightResponse.builder()
                .personIdentificator(rightResource.getSubject().getSocialSecurityNumber())
                .organizationNumber(rightResource.getReportee().getOrganizationNumber())
                .serviceCodes(getServiceCodes(rightResource.getRights()))
                .build();
    }

    private static Set<String> getServiceCodes(List<Right> rights){
        return rights.stream()
                .map(Right::getServiceCode)
                .collect(Collectors.toSet());
    }
}
