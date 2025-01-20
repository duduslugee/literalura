package br.duduslugee.literalura.client;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import org.springframework.stereotype.Component;

@Component
public class GutendexClient {

    private static final String BASE_URL = "https://gutendex.com/books/";
    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10)) // Timeout de conexão de 10 segundos
            .build();

    public String buscarLivroPorTitulo(String titulo) throws IOException {
        // Codificando o título para garantir que a URL seja válida
        String tituloCodificado = URLEncoder.encode(titulo, StandardCharsets.UTF_8);
        String url = BASE_URL + "?search=" + tituloCodificado;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .build();

        return sendRequest(request);
    }

    private String sendRequest(HttpRequest request) throws IOException {
        HttpResponse<String> response;
        try {
            response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();  // Interrompe o thread adequadamente
            throw new IOException("A requisição foi interrompida.", e);
        }

        if (response.statusCode() != 200) {
            throw new IOException("Falha na requisição. Status Code: " + response.statusCode());
        }

        return response.body();
    }
}
