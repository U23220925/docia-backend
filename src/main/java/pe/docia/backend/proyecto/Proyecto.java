package pe.docia.backend.proyecto;

import jakarta.persistence.*;
import pe.docia.backend.empresa.Empresa;

@Entity
@Table(name = "proyecto")
public class Proyecto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String codigo;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    // Getters y Setters básicos
    public Long getId() { return id; }
    
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    // Métodos agregados para solucionar el error del Controlador
    public Empresa getEmpresa() { 
        return empresa; 
    }
    
    public void setEmpresa(Empresa empresa) { 
        this.empresa = empresa; 
    }
}
