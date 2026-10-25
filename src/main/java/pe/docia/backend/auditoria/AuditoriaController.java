package pe.docia.backend.auditoria;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/auditoria")
public class AuditoriaController {

    private final AuditoriaRepository auditoriaRepository;
    private final ConsumoTokensRepository consumoTokensRepository;

    public AuditoriaController(AuditoriaRepository auditoriaRepository, ConsumoTokensRepository consumoTokensRepository) {
        this.auditoriaRepository = auditoriaRepository;
        this.consumoTokensRepository = consumoTokensRepository;
    }

    @GetMapping
    public List<Auditoria> listar() { return auditoriaRepository.findAll(); }

    @GetMapping("/tokens")
    public List<ConsumoTokens> listarConsumo(@RequestParam(required = false) Long idProyecto) {
        return idProyecto != null ? consumoTokensRepository.findByProyectoId(idProyecto) : consumoTokensRepository.findAll();
    }

    @GetMapping("/tokens/resumen")
    public ResumenTokens resumen(@RequestParam(required = false) Long idProyecto) {
        List<ConsumoTokens> lista = idProyecto != null ? consumoTokensRepository.findByProyectoId(idProyecto) : consumoTokensRepository.findAll();
        int entrada = lista.stream().mapToInt(ConsumoTokens::getTokensEntrada).sum();
        int salida = lista.stream().mapToInt(ConsumoTokens::getTokensSalida).sum();
        return new ResumenTokens(entrada, salida, lista.size());
    }
}