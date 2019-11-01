package no.difi.altinn.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ControllerAdvice
public class ExceptionGlobalHandler extends ExceptionHandlerExceptionResolver {

    @ExceptionHandler(value = {HttpClientErrorException.class, HttpServerErrorException.class})
    public ResponseEntity<Map<String, Object>> handleHttpClientExceptionFromAltinn(HttpStatusCodeException ex, WebRequest req) throws UnsupportedEncodingException {
        return new ResponseEntity<>(HttpStatus.valueOf(ex.getRawStatusCode()));
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
