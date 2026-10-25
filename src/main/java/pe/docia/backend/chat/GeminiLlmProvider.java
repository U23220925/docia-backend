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
@Order(2) // respaldo si Groq falla
public class GeminiLlmProvider implements LlmProvider {

    @Value("${docia.gemini.api-key}")
    private String apiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String getNombre() { return "Google Gemini (gemini-2.5-flash)"; }

    @Override
public LlmRespuesta generarRespuesta(String prompt) throws Exception {
    var body = mapper.createObjectNode();
    var contents = mapper.createArrayNode();
    var contenido = mapper.createObjectNode();
    var partes = mapper.createArrayNode();
    var parte = mapper.createObjectNode();
    parte.put("text", prompt);
    partes.add(parte);
    contenido.set("parts", partes);
    contents.add(contenido);
    body.set("contents", contents);

    HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"))
            .header("Content-Type", "application/json")
            .header("x-goog-api-key", apiKey)
            .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
            .build();

    HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
    if (response.statusCode() != 200) {
        throw new RuntimeException("Gemini respondio " + response.statusCode() + ": " + response.body());
    }
    JsonNode json = mapper.readTree(response.body());
    String texto = json.get("candidates").get(0).get("content").get("parts").get(0).get("text").asText();
    JsonNode usage = json.get("usageMetadata");
    int entrada = usage != null && usage.has("promptTokenCount") ? usage.get("promptTokenCount").asInt() : 0;
    int salida = usage != null && usage.has("candidatesTokenCount") ? usage.get("candidatesTokenCount").asInt() : 0;
    return new LlmRespuesta(texto, entrada, salida, getNombre());
}
}