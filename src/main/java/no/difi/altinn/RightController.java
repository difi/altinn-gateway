package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@Controller
@RequestMapping(value = "/rights")
@Slf4j
public class RightController {

    @Value("${test.rights.serviceCodes}")
    private Set<String> blackListEmptyListScope;

    @Value("${test.rights.pidServiceCodes}")
    private String pidServiceCodes;

    @Value("${test.mock.enabled:false}")
    private boolean mockEnabled;

    private final AltinnService altinnService;

    public RightController(AltinnService altinnService){
        this.altinnService = altinnService;
    }

    @GetMapping
    public ResponseEntity<RightResponse> getRoles(@RequestParam(value = "person_identificator") String personIdentificator,
                                                  @RequestParam(value = "organization_number") String organizationNumber){
        if (mockEnabled && personIdentificator.equals(pidServiceCodes)) {
            return getMockServiceCodes(personIdentificator, organizationNumber);
        } else {
            return ResponseEntity.ok(RightResponse.fromRightWrapper(altinnService.getRights(personIdentificator, organizationNumber)));
        }
    }

    private ResponseEntity<RightResponse> getMockServiceCodes(String personIdentificator, String organizationNumber) {
        return ResponseEntity.ok(RightResponse.builder()
                .personIdentificator(personIdentificator)
                .organizationNumber(organizationNumber)
                .serviceCodes(blackListEmptyListScope)
                .build());
    }
}
