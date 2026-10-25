package pe.docia.backend.chat;

import jakarta.persistence.*;
import pe.docia.backend.proyecto.Proyecto;
import java.time.LocalDateTime;

@Entity
@Table(name = "conversacion")
public class Conversacion {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "id_proyecto", nullable = false)
    private Proyecto proyecto;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio = LocalDateTime.now();

    public Long getId() { return id; }
    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }
    public LocalDateTime getFechaInicio() { return fechaInicio; }
}