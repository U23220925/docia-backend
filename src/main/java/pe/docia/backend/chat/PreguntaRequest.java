package pe.docia.backend.chat;

public class PreguntaRequest {
    private Long idProyecto;
    private Long idConversacion;
    private String pregunta;
    private Long idUsuario;
    
    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }
    public Long getIdProyecto() { return idProyecto; }
    public void setIdProyecto(Long idProyecto) { this.idProyecto = idProyecto; }
    public Long getIdConversacion() { return idConversacion; }
    public void setIdConversacion(Long idConversacion) { this.idConversacion = idConversacion; }
    public String getPregunta() { return pregunta; }
    public void setPregunta(String pregunta) { this.pregunta = pregunta; }
}