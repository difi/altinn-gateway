package no.difi.altinn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.configuration.Factory;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import javax.cache.expiry.ExpiryPolicy;
import javax.cache.expiry.ModifiedExpiryPolicy;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Configuration
@EnableCaching
//@ConditionalOnExpression("'${spring.cache.type}'!='none'")
public class CacheConfiguration {

    @Value("${cache.ttl-in-ms:5000}")
    private int ttl;

    @Bean
    public JCacheManagerCustomizer jCacheManagerCustomizer() {
        final Factory<ExpiryPolicy> expiryPolicyFactory =
                ModifiedExpiryPolicy.factoryOf(new Duration(MILLISECONDS, ttl));
        return cacheManager -> {
            cacheManager.destroyCache("delegations");
            cacheManager.createCache("delegations",
                    new MutableConfiguration<>().setExpiryPolicyFactory(expiryPolicyFactory));
        };
    }

}
