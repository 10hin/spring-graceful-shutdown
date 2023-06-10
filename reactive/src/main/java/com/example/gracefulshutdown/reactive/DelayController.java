package com.example.gracefulshutdown.reactive;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class DelayController {
    @GetMapping("/delay/{delaySeconds}")
    public Mono<ResponseEntity<Void>> delay(
            @PathVariable("delaySeconds") final Long delaySeconds
    )  {

        return Mono.delay(Duration.ofSeconds(delaySeconds))
                .map((d) -> ResponseEntity.ok().build());

    }
}
