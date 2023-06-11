package com.example.gracefulshutdown.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Function;

@SpringBootApplication
public class ClientApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClientApplication.class);

	public static void main(String[] args) {
		final var app = new SpringApplication(ClientApplication.class);
		app.setWebApplicationType(WebApplicationType.NONE);
		app.run(args);
	}

	@Bean
	public ApplicationRunner client(
			final WebClient.Builder webClientBuilder,
			@Value("${baseURL}") final String baseURL,
			@Value("${limit:#{null}}") final Long limit
	) {
		final var webClient = webClientBuilder.baseUrl(baseURL).build();
		return (appArgs) -> {
			Optional.ofNullable(limit)
					.map((l) -> Flux.interval(Duration.ofMillis(500L)).take(l))
					.orElseGet(() -> Flux.interval(Duration.ofMillis(500L)))
					.flatMap(this.request(webClient))
					.doOnNext(log -> {
						if (log.responseStatusCode.is2xxSuccessful()) {
							LOGGER.info("response status: {}, dur: {}", log.responseStatusCode, Duration.between(log.requestStart, log.responseComplete));
						} else {
							LOGGER.error("{}", log);
						}
					})
					.blockLast();
		};
	}

	private Function<Long, Mono<ExchangeLog>> request(final WebClient webClient) {
		return (l) -> {
			final var log = new ExchangeLog();
			log.requestStart = OffsetDateTime.now();
			return webClient.get()
					.uri("/delay/1")
					.retrieve()
					.toEntity(Object.class)
					.onErrorResume((t) -> Mono.just(ResponseEntity.status(503).body(t)))
					.map(resp -> {
						log.responseComplete = OffsetDateTime.now();
						log.responseStatusCode = resp.getStatusCode();
						final var body = resp.getBody();
						if (body != null) {
							log.responseDetail = body;
						}
						return log;
					});
		};
	}

	private static class ExchangeLog {
		private final UUID requestID = UUID.randomUUID();
		private transient OffsetDateTime requestStart;
		private transient OffsetDateTime responseComplete;
		private transient HttpStatusCode responseStatusCode;
		private transient Object responseDetail;
		@Override
		public String toString() {
			return "ExchangeLog(" +
					"requestID=" + this.requestID +
					", requestStart=" + this.requestStart +
					", responseComplete=" + this.responseComplete +
					", responseStatusCode=" + this.responseStatusCode +
					", responseDetail=" + this.responseDetail;
		}
	}

}
