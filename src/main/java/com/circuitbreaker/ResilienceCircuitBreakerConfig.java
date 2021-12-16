package com.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.client.circuitbreaker.ReactiveCircuitBreakerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@Slf4j
public class ResilienceCircuitBreakerConfig {

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> slowCusomtizer() {
        return factory -> {
            factory.addCircuitBreakerCustomizer(circuitBreaker -> testCustomizer());
            factory.getCircuitBreakerRegistry().getAllCircuitBreakers().forEach(circuitBreaker -> circuitBreaker.getEventPublisher().onStateTransition(
                    (event) -> {
                        if(event.getStateTransition().getToState().toString().equalsIgnoreCase("OPEN")){
                            log.info("do something ::::::");
                        }else if(event.getStateTransition().getToState().toString().equalsIgnoreCase("CLOSED")
                        || event.getStateTransition().getToState().toString().equalsIgnoreCase("HALF_OPEN")){
                            log.info("Resume something ::::::");
                        }
                    }
            ));
        };
    }

    @Bean
    public CircuitBreakerConfigCustomizer testCustomizer() {
        return CircuitBreakerConfigCustomizer
                .of("processCB", builder -> builder.slidingWindowSize(100)
                        .permittedNumberOfCallsInHalfOpenState(2)
                        .failureRateThreshold(10)
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .maxWaitDurationInHalfOpenState(Duration.ofSeconds(5))
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                        .recordExceptions(ArithmeticException.class, Exception.class));
    }

    public CircuitBreakerConfig getConfig() {
        return CircuitBreakerConfig.custom()
                .permittedNumberOfCallsInHalfOpenState(2)
                .failureRateThreshold(20)
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .maxWaitDurationInHalfOpenState(Duration.ofSeconds(5))
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.TIME_BASED)
                .recordExceptions(ArithmeticException.class, Exception.class)
                .build();

    }


}
