package pe.docia.backend.chat;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "mensaje")
public class Mensaje {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "id_conversacion", nullable = false)
    private Conversacion conversacion;

    @Column(name = "rol_emisor", nullable = false)
    private String rolEmisor;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String contenido;

    @Column(nullable = false)
    private boolean corroborado = true;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    public Long getId() { return id; }
    public Conversacion getConversacion() { return conversacion; }
    public void setConversacion(Conversacion c) { this.conversacion = c; }
    public String getRolEmisor() { return rolEmisor; }
    public void setRolEmisor(String r) { this.rolEmisor = r; }
    public String getContenido() { return contenido; }
    public void setContenido(String c) { this.contenido = c; }
    public boolean isCorroborado() { return corroborado; }
    public void setCorroborado(boolean c) { this.corroborado = c; }
    public LocalDateTime getFecha() { return fecha; }
}