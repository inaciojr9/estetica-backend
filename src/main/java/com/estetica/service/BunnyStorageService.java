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
        String url = "https://br.storage.bunnycdn.com/" + storageZone + "/" + fileName;

        System.out.println("=== DEBUG BUNNY.NET ===");
        System.out.println("1. URL de Upload montada: " + url);
        System.out.println("2. Nome da Storage Zone: " + storageZone);
        System.out.println("3. Chave de API presente? " + (apiKey != null && !apiKey.isEmpty() ? "SIM" : "NÃO"));
        System.out.println("4. Link de Puxada (CDN): " + pullUrl);

        try{
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("AccessKey", apiKey)
                    .header("Content-Type", "application/octet-stream")
                    .PUT(HttpRequest.BodyPublishers.ofInputStream(() -> fileStream))
                    .build();

            System.out.println("Disparando requisição HTTP PUT para a Bunny.net...");
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("5. Resposta do Servidor da Bunny - Código HTTP: " + response.statusCode());
            System.out.println("6. Resposta do Servidor da Bunny - Corpo: " + response.body());

            if (response.statusCode() == 201 || response.statusCode() == 200) {
                // Retorna a URL pública da CDN para salvar no banco
                String urlFinal = pullUrl + "/" + fileName;
                System.out.println("Sucesso! Link final da imagem gerado: " + urlFinal);
                return urlFinal;
            } else {
                throw new RuntimeException("Erro ao enviar arquivo para Bunny.net: " + response.body());
            }
        } catch (Exception e) {
            System.out.println("=== FALHA CRÍTICA NO UPLOAD ===");
            e.printStackTrace();
            throw new RuntimeException("Erro ao enviar arquivo para Bunny.net: " + e.getMessage());
        }
    }
}
