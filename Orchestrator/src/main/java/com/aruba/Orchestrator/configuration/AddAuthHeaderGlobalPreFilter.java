package com.aruba.Orchestrator.configuration;


import com.aruba.Orchestrator.component.OrchestratorLogger;
import com.aruba.Orchestrator.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AddAuthHeaderGlobalPreFilter implements GlobalFilter {

    @Autowired
    private UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {


        List<String> token = exchange.getRequest().getHeaders().get("Authorization");

        if (token != null && token.size() == 1) {

            OrchestratorLogger.logger.info(token.get(0));

            String appToken = userService.getLoggedUserByUsername(token.get(0)).getUsername();

            OrchestratorLogger.logger.info("appToken: " + appToken);
            ServerHttpRequest request = exchange.getRequest().mutate().header("appToken", appToken).header("appToken", appToken + "").build();

            ServerWebExchange mutatedExchange = exchange.mutate().request(request).build();
            return chain.filter(mutatedExchange);

        }

        return chain.filter(exchange);
    }
}