package pe.docia.backend.chat;

public class CitaDTO {
    private final Long idFragmento;
    private final String nombreArchivo;
    private final Integer pagina;
    private final Integer orden;

    public CitaDTO(Long idFragmento, String nombreArchivo, Integer pagina, Integer orden) {
        this.idFragmento = idFragmento;
        this.nombreArchivo = nombreArchivo;
        this.pagina = pagina;
        this.orden = orden;
    }
    public Long getIdFragmento() { return idFragmento; }
    public String getNombreArchivo() { return nombreArchivo; }
    public Integer getPagina() { return pagina; }
    public Integer getOrden() { return orden; }
}