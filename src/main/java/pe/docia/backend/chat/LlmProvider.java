package pe.docia.backend.chat;

public interface LlmProvider {
    LlmRespuesta generarRespuesta(String prompt) throws Exception;
    String getNombre();
}