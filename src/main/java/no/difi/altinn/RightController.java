package no.difi.altinn;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import no.difi.validation.OrgnrValidator;
import no.difi.validation.SsnValidator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
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
    @ApiOperation(value = "Rettigheter registrert i Altinn for en person i kontekst av en organisasjon.")
    public ResponseEntity getRoles(@ApiParam(value = "Personidentifikator") @RequestParam(value = "person_identificator") String personIdentificator,
                                   @ApiParam(value = "Organisasjonsnummer") @RequestParam(value = "organization_number") String organizationNumber){

        if(!SsnValidator.isValid(personIdentificator)) {
            log.warn("pid: "+personIdentificator+", ugyldig personidentifikator");
            return new ResponseEntity<>("ugyldig personidentifikator", HttpStatus.BAD_REQUEST);
        }

        if(!OrgnrValidator.isValid((organizationNumber))) {
            log.warn("orgnr: "+organizationNumber+", ugyldig organisasjonsnummer");
            return new ResponseEntity<>("ugyldig organisasjonsnummer", HttpStatus.BAD_REQUEST);
        }

        if (mockEnabled && personIdentificator.equals(pidServiceCodes)) {
            return getMockServiceCodes(personIdentificator, organizationNumber);
        } else {
            return ResponseEntity.ok(RightResponse.fromRightWrapper(altinnService.getRights(personIdentificator, organizationNumber)));
        }

    }

    @GetMapping(value="/servicecodes")
    @ApiOperation(value = "Rettigheter registrert i Altinn for en person i kontekst av en organisasjon fra liste.")
    public ResponseEntity getRolesWithServiceCodes(@ApiParam(value = "Personidentifikator") @RequestParam(value = "person_identificator") String personIdentificator,
                                   @ApiParam(value = "Organisasjonsnummer") @RequestParam(value = "organization_number") String organizationNumber,
                                   @ApiParam(value = "ServiceCodes") @RequestParam(value = "servicecodes") String[] serviceCodes
                                                   ){
        if (serviceCodes == null || serviceCodes.length == 0) {
            return ResponseEntity.noContent().build();
        }
        if(!SsnValidator.isValid(personIdentificator)) {
            log.warn("pid: "+personIdentificator+", ugyldig personidentifikator");
            return new ResponseEntity<>("ugyldig personidentifikator", HttpStatus.BAD_REQUEST);
        }

        if(!OrgnrValidator.isValid((organizationNumber))) {
            log.warn("orgnr: "+organizationNumber+", ugyldig organisasjonsnummer");
            return new ResponseEntity<>("ugyldig organisasjonsnummer", HttpStatus.BAD_REQUEST);
        }

        if (mockEnabled && personIdentificator.equals(pidServiceCodes)) {
            return getMockServiceCodes(personIdentificator, organizationNumber);
        } else {
            return ResponseEntity.ok(RightResponse.fromRightWrapper(altinnService.getRightsFromOData(personIdentificator, organizationNumber,serviceCodes)));
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
