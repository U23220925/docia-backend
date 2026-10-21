package pe.docia.backend.documento;

import jakarta.persistence.*;
import pe.docia.backend.proyecto.Proyecto;

import java.time.LocalDateTime;

@Entity
@Table(name = "documento")
public class Documento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @Column(name = "nombre_archivo", nullable = false)
    private String nombreArchivo;

    @Column(nullable = false)
    private String tipo; // PDF, DOCX, TXT

    @Column(name = "tamano_bytes", nullable = false)
    private Long tamanoBytes;

    @Column(name = "hash_sha256", nullable = false, length = 64)
    private String hashSha256;

    @Column(name = "estado_procesamiento", nullable = false)
    private String estadoProcesamiento; // CARGADO, INDEXADO, ERROR

    @Column(name = "fecha_carga", nullable = false)
    private LocalDateTime fechaCarga = LocalDateTime.now();

    public Long getId() { return id; }
    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String n) { this.nombreArchivo = n; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public Long getTamanoBytes() { return tamanoBytes; }
    public void setTamanoBytes(Long t) { this.tamanoBytes = t; }
    public String getHashSha256() { return hashSha256; }
    public void setHashSha256(String h) { this.hashSha256 = h; }
    public String getEstadoProcesamiento() { return estadoProcesamiento; }
    public void setEstadoProcesamiento(String e) { this.estadoProcesamiento = e; }
    public LocalDateTime getFechaCarga() { return fechaCarga; }
}