package no.difi.altinn;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.domain.Delegation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping(value = "/delegations")
@Slf4j
public class DelegationController {

    @Value("${test.blacklist.404}")
    private String blackListEmptyListScope;

    @Value("${test.blacklist.503}")
    private String blackListAltinnUnavailableScope;

    @Value("${test.mock.enabled:false}")
    private boolean mockEnabled;

    private final AltinnService altinnService;

    public DelegationController(AltinnService altinnService) {
        this.altinnService = altinnService;
    }

    @GetMapping
    @ApiOperation(value = "Delegeringer fra en sluttbruker, for eksempel en kommune, til en tjenesteleverandør registrert i Altinn.")
    public ResponseEntity<List<Delegation>> getDelegations(@ApiParam(value = "Scope") @RequestParam(name = "scope") String scope,
                                                           @ApiParam(value = "Konsumentens organisasjonsnummer") @RequestParam(name = "consumer_org", required = false) String consumerOrg,
                                                           @ApiParam(value = "Leverandørens organisasjonsnummer") @RequestParam(name = "supplier_org", required = false) String supplierOrg) {
        if (mockEnabled) {
            return getMockDelegations(scope, consumerOrg, supplierOrg);
        } else {
            return ResponseEntity.ok(altinnService.getDelegations(scope, consumerOrg, supplierOrg));
        }
    }

    private ResponseEntity<List<Delegation>> getMockDelegations(@RequestParam(name = "scope") String scope, @RequestParam(name = "consumer_org") String consumerOrg, @RequestParam(name = "supplier_org") String supplierOrg) {
        if (isBlackListEmptyList(scope)) {
            return ResponseEntity.ok(new ArrayList<>());
        }
        if (isBlackListAltinnUnavailable(scope)) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        String[] scopes = new String[]{scope};
        Delegation delegation = Delegation.builder()
                .scopes(scopes)
                .consumerOrgnr(consumerOrg)
                .supplierOrgnr(supplierOrg)
                .delegationScheme("delegationScheme")
                .build();
        List<Delegation> delegations = Collections.singletonList(delegation);
        return ResponseEntity.ok(delegations);
    }

    private boolean isBlackListEmptyList(String scope) {
        return blackListEmptyListScope != null && !blackListEmptyListScope.isEmpty() && scope.contains(blackListEmptyListScope);
    }

    private boolean isBlackListAltinnUnavailable(String scope) {
        return blackListAltinnUnavailableScope != null && !blackListAltinnUnavailableScope.isEmpty() && scope.contains(blackListAltinnUnavailableScope);
    }
}
