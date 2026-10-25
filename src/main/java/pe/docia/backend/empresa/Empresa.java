package pe.docia.backend.empresa;

import jakarta.persistence.*;

@Entity
@Table(name = "empresa")
public class Empresa {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "razon_social", nullable = false)
    private String razonSocial;

    @Column(nullable = false, unique = true)
    private String ruc;

    @Column(nullable = false)
    private String plan = "Basico";

    @Column(nullable = false)
    private String estado = "ACTIVO";

    public Long getId() { return id; }
    public String getRazonSocial() { return razonSocial; }
    public void setRazonSocial(String r) { this.razonSocial = r; }
    public String getRuc() { return ruc; }
    public void setRuc(String ruc) { this.ruc = ruc; }
    public String getPlan() { return plan; }
    public void setPlan(String plan) { this.plan = plan; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}