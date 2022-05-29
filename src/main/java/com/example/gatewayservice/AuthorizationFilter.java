package com.example.gatewayservice;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationFilter extends AbstractGatewayFilterFactory<AuthorizationFilter.Config> {

    private Environment env;

    public AuthorizationFilter(Environment env){
        super(Config.class);
        this.env = env;
    }

    @Override
    public GatewayFilter apply(Config config) {
        return ((exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            ServerHttpResponse response = exchange.getResponse();
             log.info("Pre Auth Filter");

            if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)){
               return onError(exchange, "No Authorization Header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            String token = authHeader.replace("Bearer", "");

            if (!isJwtValid(token)){
                return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
            }
            return chain.filter(exchange);
//            return chain.filter(exchange).then(Mono.fromRunnable(()->{
//                log.info("Auth filter : response code -> {}", response.getStatusCode());
//            }));
        });

    }

    private boolean isJwtValid(String jwt) {
        boolean returnValue = true;

        String subject = null;

        try {
            subject = Jwts
                    .parser()
                    .setSigningKey(env.getProperty("token.secret"))
                    .parseClaimsJws(jwt).getBody()
                    .getSubject();
        } catch (Exception ex) {
            returnValue = false;
        }

        if (subject == null || subject.isEmpty()) {
            returnValue = false;
        }

        return returnValue;
    }

//    private boolean isValidToken(String token) {
//        String subject = null;
//
//        try {
//            subject = Jwts.parser().setSigningKey(env.getProperty("token.secret"))
//                    .parseClaimsJws(token).getBody()
//                    .getSubject();
//        }catch (Exception e){
//            return false;
//        }
//
//        if (subject == null || subject.isEmpty()){
//            return false;
//        }
//
//        return true;
//    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus){
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        log.error(err);

        return response.setComplete();
    }

    public static class Config {

    }
}
