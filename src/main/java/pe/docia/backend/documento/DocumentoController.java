package pe.docia.backend.documento;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/documentos")
public class DocumentoController {

    private final DocumentoService documentoService;

    public DocumentoController(DocumentoService documentoService) {
        this.documentoService = documentoService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public Documento subir(@RequestParam Long idProyecto, @RequestParam MultipartFile archivo) throws IOException {
        return documentoService.subirDocumento(idProyecto, archivo);
    }
}