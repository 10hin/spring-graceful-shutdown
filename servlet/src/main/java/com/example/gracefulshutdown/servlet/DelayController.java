package com.example.gracefulshutdown.servlet;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
public class DelayController {
    @GetMapping("/delay/{delaySeconds}")
    public ResponseEntity<Void> delay(
            @PathVariable("delaySeconds") final Long delaySeconds
    ) {

        try {
            Thread.sleep(Duration.ofSeconds(delaySeconds).toMillis());
        } catch (InterruptedException e) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }
        return ResponseEntity.ok().build();

    }
}
