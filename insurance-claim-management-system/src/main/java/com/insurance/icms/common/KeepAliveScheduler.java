package com.insurance.icms.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class KeepAliveScheduler {

	private static final Logger log = LoggerFactory.getLogger(KeepAliveScheduler.class);

	private final String baseUrl;
	private final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(10)).build();

	public KeepAliveScheduler(@Value("${app.keep-alive.base-url:}") String baseUrl) {
		this.baseUrl = baseUrl;
	}

	@Scheduled(initialDelay = 60_000, fixedRate = 300_000)
	public void ping() {

		if (baseUrl == null || baseUrl.isBlank()) {
			return;
		}

		try {
			HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/health"))
					.timeout(Duration.ofSeconds(15)).GET().build();

			HttpResponse<Void> response = client.send(request, HttpResponse.BodyHandlers.discarding());

			log.info("Keep-alive ping -> {}", response.statusCode());

		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		} catch (Exception e) {
			log.warn("Keep-alive ping failed: {}", e.getMessage());
		}
	}
}