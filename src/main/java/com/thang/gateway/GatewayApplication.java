package com.thang.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
		return builder.routes()
				.route("user-route", r -> r.path("/user/**")
						.filters(f -> f.stripPrefix(1)
								.circuitBreaker(c -> c.setName("CircuitBreaker")
										.getFallbackUri()))
						.uri("lb://account-service"))
				.route("report-route", r -> r.path("/report/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://statistic-service"))
				.route("notification-route", r -> r.path("/notification/**")
						.filters(f -> f.stripPrefix(1))
						.uri("lb://notification-service"))

				.route("openapi", r -> r.path("/v3/api-docs/**")
						.filters(f -> f.rewritePath("/v3/api-docs/(?<service>.*)", "/${service}/v3/api-docs"))
						.uri("lb://gateway-service"))
				.build();
	}
}
