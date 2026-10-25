package pe.docia.backend.auditoria;

import jakarta.persistence.*;
import pe.docia.backend.proyecto.Proyecto;
import pe.docia.backend.usuario.Usuario;
import java.time.LocalDateTime;

@Entity
@Table(name = "consumo_tokens")
public class ConsumoTokens {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne @JoinColumn(name = "id_proyecto")
    private Proyecto proyecto;

    @ManyToOne @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(nullable = false)
    private String modelo;

    @Column(name = "tokens_entrada", nullable = false)
    private int tokensEntrada;

    @Column(name = "tokens_salida", nullable = false)
    private int tokensSalida;

    @Column(nullable = false)
    private LocalDateTime fecha = LocalDateTime.now();

    public Long getId() { return id; }
    public Proyecto getProyecto() { return proyecto; }
    public void setProyecto(Proyecto proyecto) { this.proyecto = proyecto; }
    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public int getTokensEntrada() { return tokensEntrada; }
    public void setTokensEntrada(int t) { this.tokensEntrada = t; }
    public int getTokensSalida() { return tokensSalida; }
    public void setTokensSalida(int t) { this.tokensSalida = t; }
    public LocalDateTime getFecha() { return fecha; }
}