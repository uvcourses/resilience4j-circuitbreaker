package com.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

import javax.websocket.server.PathParam;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    @Autowired
    private ReactiveResilience4JCircuitBreakerFactory reactiveResilience4JCircuitBreakerFactory;
    @GetMapping("/triggerCircuitBreaker")
    public Mono<String> triggerCircuitBreaker(@PathParam("a") int a,@PathParam("b") int b) throws Exception {
        CircuitBreaker circuitBreaker;
        circuitBreaker=reactiveResilience4JCircuitBreakerFactory.getCircuitBreakerRegistry().circuitBreaker("processCB");
        circuitBreaker.decorateCallable( ()->a/b).call();
        return Mono.just("Hello How Are You ?");
    }
    public Mono<String> toCook() {
        return Mono.just("Hello Sir");
    }
}
