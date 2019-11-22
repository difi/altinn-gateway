package no.difi.altinn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.domain.Delegation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping(value = "/delegation")
@Slf4j
@RequiredArgsConstructor
public class DelegationController {

    @Value("${test.blacklist.404}")
    private String blackListEmptyListScope;

    @Value("${test.blacklist.503}")
    private String blackListAltinnUnavailableScope;

    @GetMapping(params = {"scope", "consumer_org", "supplier_org"})
    public ResponseEntity<List<Delegation>> getDelegation(@RequestParam(name = "scope") String scope,
                                                         @RequestParam(name = "consumer_org") String consumerOrg,
                                                         @RequestParam(name = "supplier_org") String supplierOrg) {
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
        //TODO: Kall Altinn
        return ResponseEntity.ok(delegations);
    }

    private boolean isBlackListEmptyList(String scope) {
        return blackListEmptyListScope != null && !blackListEmptyListScope.isEmpty() && scope.contains(blackListEmptyListScope);
    }

    private boolean isBlackListAltinnUnavailable(String scope) {
        return blackListAltinnUnavailableScope != null && !blackListAltinnUnavailableScope.isEmpty() && scope.contains(blackListAltinnUnavailableScope);
    }
}
