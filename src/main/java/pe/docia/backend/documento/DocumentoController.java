package pe.docia.backend.documento;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoRepository documentoRepository;
    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService, DocumentoRepository documentoRepository) {
        this.documentoService = documentoService;
        this.documentoRepository = documentoRepository;
    }

    @PostMapping(consumes = "multipart/form-data")
    public Documento subir(@RequestParam Long idProyecto, @RequestParam MultipartFile archivo,
                            @RequestParam(required = false) Long idUsuario) throws IOException {
        return documentoService.subirDocumento(idProyecto, archivo, idUsuario);
    }

    @GetMapping
    public List<Documento> listar(@RequestParam Long idProyecto) {
        return documentoRepository.findByProyectoId(idProyecto);
    }
}