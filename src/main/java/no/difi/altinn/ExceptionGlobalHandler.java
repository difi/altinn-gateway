package no.difi.altinn;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

@Slf4j
public class ExceptionGlobalHandler extends ExceptionHandlerExceptionResolver {

    @ExceptionHandler({HttpClientErrorException.class})
    public ResponseEntity handleURISyntaxException(HttpClientErrorException e) {
        log.error("Error response from Altinn: " + e.getRawStatusCode() + " - " + e.getResponseBodyAsString());
        return new ResponseEntity(HttpStatus.BAD_GATEWAY);
    }

}
