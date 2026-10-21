package pe.docia.backend.proyecto;

import org.springframework.web.bind.annotation.*;
import pe.docia.backend.documento.DocumentoRepository; 

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final DocumentoRepository documentoRepository; // 1. DECLARAR LA VARIABLE

    // 2. INYECTAR EN EL CONSTRUCTOR (Ambos repositorios/servicios van separados por coma)
    public ProyectoController(ProyectoService proyectoService, DocumentoRepository documentoRepository) {
        this.proyectoService = proyectoService;
        this.documentoRepository = documentoRepository;
    }

    @PostMapping
    public Proyecto crear(@RequestBody Proyecto proyecto) {
        return proyectoService.crear(proyecto);
    }

    @GetMapping
    public List<Proyecto> listar() {
        return proyectoService.listarTodos();
    }

    @GetMapping("/{id}/conteo-documentos")
    public long contarDocumentos(@PathVariable Long id) {
        return documentoRepository.countByProyectoId(id); 
    }
}
