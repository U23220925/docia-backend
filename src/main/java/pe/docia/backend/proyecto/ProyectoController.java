package pe.docia.backend.proyecto;

import org.springframework.web.bind.annotation.*;
import pe.docia.backend.empresa.Empresa;
import pe.docia.backend.empresa.EmpresaRepository;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
public class ProyectoController {

    private final ProyectoService proyectoService;
    private final EmpresaRepository empresaRepository;

    public ProyectoController(ProyectoService proyectoService, EmpresaRepository empresaRepository) {
        this.proyectoService = proyectoService;
        this.empresaRepository = empresaRepository;
    }

    @PostMapping
    public Proyecto crear(@RequestBody ProyectoRequest request) {
        Proyecto proyecto = new Proyecto();
        proyecto.setNombre(request.getNombre());
        proyecto.setCodigo(request.getCodigo());

        if (request.getIdEmpresa() != null) {
            Empresa empresa = empresaRepository.findById(request.getIdEmpresa())
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada: " + request.getIdEmpresa()));
            proyecto.setEmpresa(empresa);
        }

        return proyectoService.crear(proyecto);
    }

    @GetMapping
    public List<Proyecto> listar(@RequestParam(required = false) Long idEmpresa) {
        return idEmpresa != null ? proyectoService.listarPorEmpresa(idEmpresa) : proyectoService.listarTodos();
    }
}
