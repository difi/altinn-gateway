package no.difi.altinn;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.domain.Delegation;
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

    @GetMapping(params = {"scope", "consumer_org", "supplier_org"}, consumes = "application/x-www-form-urlencoded")
    public ResponseEntity<List<Delegation>> getDelegation(@RequestParam(name = "scope") String scope,
                                                         @RequestParam(name = "consumer_org") String consumerOrg,
                                                         @RequestParam(name = "supplier_org") String supplierOrg) {
        String[] scopes = new String[]{"scope1", "scope2", scope};
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
}
