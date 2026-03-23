package com.openjoyer.openmarket.api_gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@Order(1000)
public class AccessLogFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        long startedMs = System.currentTimeMillis();

        return chain.filter(exchange)
                .doFinally(signal -> {
                    long durationMs = System.currentTimeMillis() - startedMs;
                    HttpStatusCode status = exchange.getResponse().getStatusCode();
                    String requestId = exchange.getRequest().getHeaders().getFirst("X-Request-Id");

                    log.info("requestId={} method={} path={} status={} durationMs={}",
                            requestId,
                            exchange.getRequest().getMethod(),
                            exchange.getRequest().getURI().getPath(),
                            status,
                            durationMs);
                });
    }
}
