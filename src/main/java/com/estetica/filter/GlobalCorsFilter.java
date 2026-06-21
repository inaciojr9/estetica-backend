package com.estetica.filter;

import org.jboss.resteasy.reactive.server.ServerRequestFilter;
import org.jboss.resteasy.reactive.server.ServerResponseFilter;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.Response;

@ApplicationScoped
public class GlobalCorsFilter {

    // 🔒 1. Injeta os cabeçalhos de CORS em todas as respostas usando a API padrão do Jakarta
    @ServerResponseFilter
    public void aplicarCorsGlobal(ContainerResponseContext responseContext) {
        responseContext.getHeaders().add("Access-Control-Allow-Origin", "*");
        responseContext.getHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        responseContext.getHeaders().add("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Origin");
        responseContext.getHeaders().add("Access-Control-Allow-Credentials", "true");
    }

    // 🔀 2. Intercepta e responde na hora as requisições de teste OPTIONS (Preflight)
    @ServerRequestFilter(preMatching = true)
    public Response interceptarOptions(ContainerRequestContext requestContext) {
        if ("OPTIONS".equalsIgnoreCase(requestContext.getMethod())) {
            return Response.ok()
                    .header("Access-Control-Allow-Origin", "*")
                    .header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
                    .header("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Origin")
                    .header("Access-Control-Allow-Credentials", "true")
                    .build();
        }
        return null;
    }
}
