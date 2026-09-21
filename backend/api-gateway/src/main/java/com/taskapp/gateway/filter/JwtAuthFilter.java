package com.taskapp.gateway.filter;

import com.taskapp.common.dto.TokenClaims;
import com.taskapp.common.security.JwtUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    private final JwtUtil jwtUtil;

    private static final List<String> PUBLIC_PREFIXES = List.of("/auth/", "/actuator/");

    public JwtAuthFilter(@Value("${app.jwt.secret}") String secret,
                         @Value("${app.jwt.expiration-seconds:86400}") long expiration) {
        this.jwtUtil = new JwtUtil(secret, expiration);
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Strip any incoming identity headers to prevent spoofing
        ServerHttpRequest sanitized = exchange.getRequest().mutate()
                .headers(h -> {
                    h.remove("X-User-Id");
                    h.remove("X-Username");
                    h.remove("X-Role");
                })
                .build();

        boolean isPublic = PUBLIC_PREFIXES.stream().anyMatch(path::startsWith);
        if (isPublic) {
            return chain.filter(exchange.mutate().request(sanitized).build());
        }

        String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            return unauthorized(exchange);
        }
        String token = auth.substring(7);
        try {
            TokenClaims claims = jwtUtil.parse(token);
            ServerHttpRequest withHeaders = sanitized.mutate()
                    .header("X-User-Id", String.valueOf(claims.userId()))
                    .header("X-Username", claims.username())
                    .header("X-Role", claims.role().name())
                    .build();
            return chain.filter(exchange.mutate().request(withHeaders).build());
        } catch (Exception e) {
            e.printStackTrace();
            return unauthorized(exchange);
        }
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    @Override
    public int getOrder() {
        return -100;
    }
}