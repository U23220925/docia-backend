package pe.docia.backend.documento;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.docia.backend.proyecto.Proyecto;
import pe.docia.backend.proyecto.ProyectoRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final ProyectoRepository proyectoRepository;
    private final DocumentHashService hashService;
    private final DocxTextExtractor docxExtractor;
    private final FragmentoRepository fragmentoRepository;

    public DocumentoService(DocumentoRepository documentoRepository,
                             ProyectoRepository proyectoRepository,
                             DocumentHashService hashService,
                             DocxTextExtractor docxExtractor,
                             FragmentoRepository fragmentoRepository) { 
        this.documentoRepository = documentoRepository;
        this.proyectoRepository = proyectoRepository;
        this.hashService = hashService;
        this.docxExtractor = docxExtractor;
        this.fragmentoRepository = fragmentoRepository;
    }

    public Documento subirDocumento(Long idProyecto, MultipartFile archivo) throws IOException {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Path rutaTemp = Path.of("uploads", archivo.getOriginalFilename());
        Files.createDirectories(rutaTemp.getParent());
        Files.copy(archivo.getInputStream(), rutaTemp, StandardCopyOption.REPLACE_EXISTING);
        File archivoGuardado = rutaTemp.toFile();

        String hash = hashService.calcularHashSha256(archivoGuardado);
        documentoRepository.findByHashSha256(hash).ifPresent(d -> {
            throw new RuntimeException("Este documento ya fue cargado antes (duplicado)");
        });

        String nombreArchivo = archivo.getOriginalFilename();
        List<String> parrafos;
        
        if (nombreArchivo.endsWith(".docx")) {
            parrafos = docxExtractor.extraerParrafos(archivoGuardado.getPath());
        } else if (nombreArchivo.endsWith(".txt")) {
            parrafos = Files.readAllLines(rutaTemp).stream().filter(l -> !l.isBlank()).toList();
        } else {
            parrafos = List.of();
        }

        Documento documento = new Documento();
        documento.setProyecto(proyecto);
        documento.setNombreArchivo(nombreArchivo);
        documento.setTipo(nombreArchivo.endsWith(".docx") ? "DOCX" : nombreArchivo.endsWith(".txt") ? "TXT" : "OTRO");
        documento.setTamanoBytes(archivo.getSize());
        documento.setHashSha256(hash);
        documento.setEstadoProcesamiento(!parrafos.isEmpty() ? "INDEXADO" : "CARGADO");
        documento = documentoRepository.save(documento);

        for (int i = 0; i < parrafos.size(); i++) {
            Fragmento f = new Fragmento();
            f.setDocumento(documento);
            f.setOrden(i + 1);
            f.setTexto(parrafos.get(i));
            fragmentoRepository.save(f);
        }

        return documento;
    }
}
