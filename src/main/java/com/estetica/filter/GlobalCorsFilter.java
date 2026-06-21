package com.estetica.filter;

import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;
import org.jboss.resteasy.reactive.server.spi.ServerHttpResponse;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.core.Response;

import java.util.List;

public class GlobalCorsFilter {

    // 🔒 1. Injeta os cabeçalhos de CORS em 100% das respostas bem-sucedidas do Quarkus REST
    @ServerResponseFilter
    public static void aplicarCorsGlobal(ServerHttpResponse response) {
        response.setResponseHeader("Access-Control-Allow-Origin", List.of("*"));
        response.setResponseHeader("Access-Control-Allow-Methods", List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "HEAD"));
        response.setResponseHeader("Access-Control-Allow-Headers", List.of("Content-Type", "Authorization", "Accept", "Origin"));
        response.setResponseHeader("Access-Control-Allow-Credentials", List.of("true"));
    }

    // 🔀 2. Intercepta e responde na hora as requisições OPTIONS (Preflight) do navegador
    @ServerRequestFilter(preMatching = true)
    public static Response interceptarOptions(ContainerRequestContext requestContext) {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Origin")
                    .header("Access-Control-Allow-Credentials", "true")
                    .build();
        }
        return null; // Deixa a requisição seguir em frente se for GET, POST, etc.
    }
}
