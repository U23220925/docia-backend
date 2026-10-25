package pe.docia.backend.documento;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/fragmentos")
public class FragmentoController {

    private final FragmentoRepository fragmentoRepository;

    public FragmentoController(FragmentoRepository fragmentoRepository) {
        this.fragmentoRepository = fragmentoRepository;
    }

    @GetMapping("/{id}")
    public Fragmento obtener(@PathVariable Long id) {
        return fragmentoRepository.findById(id).orElseThrow(() -> new RuntimeException("Fragmento no encontrado"));
    }
}