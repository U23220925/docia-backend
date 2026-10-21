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

@Service
public class DocumentoService {

    private final DocumentoRepository documentoRepository;
    private final ProyectoRepository proyectoRepository;
    private final DocumentHashService hashService;
    private final DocxTextExtractor docxExtractor;

    public DocumentoService(DocumentoRepository documentoRepository,
                             ProyectoRepository proyectoRepository,
                             DocumentHashService hashService,
                             DocxTextExtractor docxExtractor) {
        this.documentoRepository = documentoRepository;
        this.proyectoRepository = proyectoRepository;
        this.hashService = hashService;
        this.docxExtractor = docxExtractor;
    }

    public Documento subirDocumento(Long idProyecto, MultipartFile archivo) throws IOException {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        // Guarda el archivo temporalmente para poder calcular el hash y leerlo
        Path rutaTemp = Path.of("uploads", archivo.getOriginalFilename());
        Files.createDirectories(rutaTemp.getParent());
        Files.copy(archivo.getInputStream(), rutaTemp, StandardCopyOption.REPLACE_EXISTING);
        File archivoGuardado = rutaTemp.toFile();

        // 1) Google Guava: calcula el hash y evita duplicados
        String hash = hashService.calcularHashSha256(archivoGuardado);
        documentoRepository.findByHashSha256(hash).ifPresent(d -> {
            throw new RuntimeException("Este documento ya fue cargado antes (duplicado)");
        });

        // 2) Si es .docx, Apache POI extrae el texto (por ahora solo se cuenta; el chunking real viene despues)
        int parrafosExtraidos = 0;
        if (archivo.getOriginalFilename().endsWith(".docx")) {
            parrafosExtraidos = docxExtractor.extraerParrafos(archivoGuardado.getPath()).size();
        }

        Documento documento = new Documento();
        documento.setProyecto(proyecto);
        documento.setNombreArchivo(archivo.getOriginalFilename());
        documento.setTipo(archivo.getOriginalFilename().endsWith(".docx") ? "DOCX" : "OTRO");
        documento.setTamanoBytes(archivo.getSize());
        documento.setHashSha256(hash);
        documento.setEstadoProcesamiento(parrafosExtraidos > 0 ? "INDEXADO" : "CARGADO");

        return documentoRepository.save(documento);
    }
}