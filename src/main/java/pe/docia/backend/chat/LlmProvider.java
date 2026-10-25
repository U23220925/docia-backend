package pe.docia.backend.chat;

public interface LlmProvider {
    String generarRespuesta(String prompt) throws Exception;
    String getNombre();
}