package pe.docia.backend.empresa;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/empresas")
public class EmpresaController {

    private final EmpresaRepository empresaRepository;

    public EmpresaController(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    @PostMapping
    public Empresa crear(@RequestBody Empresa empresa) { return empresaRepository.save(empresa); }

    @GetMapping
    public List<Empresa> listar() { return empresaRepository.findAll(); }
}