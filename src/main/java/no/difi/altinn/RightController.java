package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import no.difi.altinn.domain.RightResource;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping(value = "/rights")
@Slf4j
public class RightController {

    private final AltinnService altinnService;

    public RightController(AltinnService altinnService){
        this.altinnService = altinnService;
    }

    @GetMapping
    public ResponseEntity<RightResponse> getRoles(@RequestParam(value = "person_identificator") String personIdentificator,
                                                  @RequestParam(value = "organization_number") String organizationNumber){
        return ResponseEntity.ok(RightResponse.fromRightWrapper(altinnService.getRights(personIdentificator, organizationNumber)));
    }
}
