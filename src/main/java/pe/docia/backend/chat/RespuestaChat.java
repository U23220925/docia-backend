package pe.docia.backend.chat;

import java.util.List;

public class RespuestaChat {
    private final Long idConversacion;
    private final String respuesta;
    private final boolean corroborado;
    private final List<CitaDTO> citas;

    public RespuestaChat(Long idConversacion, String respuesta, boolean corroborado, List<CitaDTO> citas) {
        this.idConversacion = idConversacion;
        this.respuesta = respuesta;
        this.corroborado = corroborado;
        this.citas = citas;
    }
    public Long getIdConversacion() { return idConversacion; }
    public String getRespuesta() { return respuesta; }
    public boolean isCorroborado() { return corroborado; }
    public List<CitaDTO> getCitas() { return citas; }
}