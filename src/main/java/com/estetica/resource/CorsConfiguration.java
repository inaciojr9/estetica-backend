package com.estetica.resource;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.CorsHandler;

@ApplicationScoped
public class CorsConfiguration {

    public void init(@Observes Router router) {
        router.route().handler(CorsHandler.create()
                .addRelativeOrigin(".*") // Permite qualquer origem (inclusive localhost)
                .allowedMethod(io.vertx.core.http.HttpMethod.GET)
                .allowedMethod(io.vertx.core.http.HttpMethod.POST)
                .allowedMethod(io.vertx.core.http.HttpMethod.PUT)
                .allowedMethod(io.vertx.core.http.HttpMethod.DELETE)
                .allowedMethod(io.vertx.core.http.HttpMethod.OPTIONS)
                .allowedHeader("Content-Type")
                .allowedHeader("Authorization")
                .allowedHeader("Accept")
                .allowedHeader("Origin"));
    }
}
