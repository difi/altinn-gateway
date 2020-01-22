package no.difi.altinn;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class AltinnGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(AltinnGatewayApplication.class, args);
    }

}
