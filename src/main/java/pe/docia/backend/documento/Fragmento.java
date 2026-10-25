package pe.docia.backend.documento;

import jakarta.persistence.*;

@Entity
@Table(name = "fragmento")
public class Fragmento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_documento", nullable = false)
    private Documento documento;

    @Column(nullable = false)
    private Integer orden;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    public Long getId() { return id; }
    public Documento getDocumento() { return documento; }
    public void setDocumento(Documento documento) { this.documento = documento; }
    public Integer getOrden() { return orden; }
    public void setOrden(Integer orden) { this.orden = orden; }
    public String getTexto() { return texto; }
    public void setTexto(String texto) { this.texto = texto; }
}