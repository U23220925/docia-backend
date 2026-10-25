package pe.docia.backend.chat;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
@Order(1) // se intenta primero
public class GroqLlmProvider implements LlmProvider {

    @Value("${docia.groq.api-key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getNombre() { return "Groq (openai/gpt-oss-120b)"; }

    @Override
    public String generarRespuesta(String prompt) throws Exception {
        var body = mapper.createObjectNode();
        body.put("model", "openai/gpt-oss-120b");
        var mensajes = mapper.createArrayNode();
        var mensaje = mapper.createObjectNode();
        mensaje.put("role", "user");
        mensaje.put("content", prompt);
        mensajes.add(mensaje);
        body.set("messages", mensajes);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new RuntimeException("Groq respondio " + response.statusCode() + ": " + response.body());
        }
        JsonNode json = mapper.readTree(response.body());
        return json.get("choices").get(0).get("message").get("content").asText();
    }
}