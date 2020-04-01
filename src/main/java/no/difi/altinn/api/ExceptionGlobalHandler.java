package no.difi.altinn.api;

import lombok.extern.slf4j.Slf4j;
import no.difi.resilience.annotation.DisableResilient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ExceptionGlobalHandler extends ExceptionHandlerExceptionResolver {

    @ExceptionHandler(value = {HttpClientErrorException.class, HttpServerErrorException.class})
    @DisableResilient
    public ResponseEntity handleHttpClientExceptionFromAltinn(HttpStatusCodeException ex, WebRequest req) {
        if (HttpStatus.BAD_REQUEST == ex.getStatusCode()) {
            String response = ex.getResponseBodyAsString() == null ? ex.getResponseBodyAsString() : ex.getStatusText();
            log.warn("Response from Altinn: " + ex.getRawStatusCode() + " - " + response + " for request " + req.toString());
            return new ResponseEntity<>(null, HttpStatus.OK);
        } else {
            log.error("Error response from Altinn: " + ex.getRawStatusCode() + " - " + ex.getMessage() + " for request " + req.toString());
            return new ResponseEntity<>("Altinn-api not available: " + ex.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
        }
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest req) {
        Map<String, Object> errorMap = new HashMap<>();
        errorMap.put("timestamp", LocalDateTime.now().toString());
        errorMap.put("status", 400);
        errorMap.put("error", "IllegalArgumentException");
        errorMap.put("message", ex.getMessage());
        errorMap.put("path", ((ServletWebRequest) req).getRequest().getRequestURI());
        return new ResponseEntity<>(errorMap, HttpStatus.BAD_REQUEST);
    }

}
