package com.estetica.service;

import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.UUID;

@ApplicationScoped
public class BunnyStorageService {

    @ConfigProperty(name = "bunny.storage.zone")
    String storageZone;

    @ConfigProperty(name = "bunny.api.key")
    String apiKey;

    @ConfigProperty(name = "bunny.pull.url")
    String pullUrl;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    public String uploadFile(InputStream fileStream, String originalFileName) throws Exception {
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));
        String fileName = UUID.randomUUID().toString() + extension;
        
        // URL de Upload da Bunny: https://bunnycdn.com
        String url = "https://bunnycdn.com" + storageZone + "/" + fileName;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("AccessKey", apiKey)
                .header("Content-Type", "application/octet-stream")
                .PUT(HttpRequest.BodyPublishers.ofInputStream(() -> fileStream))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 201 || response.statusCode() == 200) {
            // Retorna a URL pública da CDN para salvar no banco
            return pullUrl + "/" + fileName;
        } else {
            throw new RuntimeException("Erro ao enviar arquivo para Bunny.net: " + response.body());
        }
    }
}
