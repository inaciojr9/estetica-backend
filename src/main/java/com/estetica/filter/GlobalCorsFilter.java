package com.estetica.filter;

import org.jboss.resteasy.reactive.server.ServerResponseFilter;
import org.jboss.resteasy.reactive.server.spi.ServerHttpResponse;

public class GlobalCorsFilter {

    // 🔒 Este método intercepta 100% das requisições e respostas do Quarkus REST automaticamente
    @ServerResponseFilter
    public static void aplicarCorsGlobal(ServerHttpResponse response) {
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.addHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD");
        response.addHeader("Access-Control-Allow-Headers", "Content-Type, Authorization, Accept, Origin");
        response.addHeader("Access-Control-Allow-Credentials", "true");
    }
}
