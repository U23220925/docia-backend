package pe.docia.backend.chat;

import java.util.List;

public class RespuestaChat {
    private Long idConversacion;
    private String respuesta;
    private boolean corroborado;
    private List<String> citas;

    public RespuestaChat(Long idConversacion, String respuesta, boolean corroborado, List<String> citas) {
        this.idConversacion = idConversacion;
        this.respuesta = respuesta;
        this.corroborado = corroborado;
        this.citas = citas;
    }
    public Long getIdConversacion() { return idConversacion; }
    public String getRespuesta() { return respuesta; }
    public boolean isCorroborado() { return corroborado; }
    public List<String> getCitas() { return citas; }
}