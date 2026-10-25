package pe.docia.backend.documento;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pe.docia.backend.auditoria.AuditoriaService;
import pe.docia.backend.proyecto.Proyecto;
import pe.docia.backend.proyecto.ProyectoRepository;
import pe.docia.backend.usuario.UsuarioRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentoService {

    private record FragmentoTexto(Integer pagina, String texto) {}

    private final DocumentoRepository documentoRepository;
    private final ProyectoRepository proyectoRepository;
    private final FragmentoRepository fragmentoRepository;
    private final DocumentHashService hashService;
    private final DocxTextExtractor docxExtractor;
    private final PdfTextExtractor pdfExtractor;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;

    public DocumentoService(DocumentoRepository documentoRepository, ProyectoRepository proyectoRepository,
                             FragmentoRepository fragmentoRepository, DocumentHashService hashService,
                             DocxTextExtractor docxExtractor, PdfTextExtractor pdfExtractor,
                             UsuarioRepository usuarioRepository, AuditoriaService auditoriaService) {
        this.documentoRepository = documentoRepository;
        this.proyectoRepository = proyectoRepository;
        this.fragmentoRepository = fragmentoRepository;
        this.hashService = hashService;
        this.docxExtractor = docxExtractor;
        this.pdfExtractor = pdfExtractor;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
    }

    public Documento subirDocumento(Long idProyecto, MultipartFile archivo, Long idUsuario) throws IOException {
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
        List<FragmentoTexto> fragmentosTexto = new ArrayList<>();
        String tipo;

        if (nombreArchivo.endsWith(".docx")) {
            tipo = "DOCX";
            for (String p : docxExtractor.extraerParrafos(archivoGuardado.getPath())) {
                fragmentosTexto.add(new FragmentoTexto(null, p));
            }
        } else if (nombreArchivo.endsWith(".pdf")) {
            tipo = "PDF";
            for (var fp : pdfExtractor.extraerPorPagina(archivoGuardado)) {
                fragmentosTexto.add(new FragmentoTexto(fp.pagina(), fp.texto()));
            }
        } else if (nombreArchivo.endsWith(".txt")) {
            tipo = "TXT";
            for (String l : Files.readAllLines(rutaTemp)) {
                if (!l.isBlank()) fragmentosTexto.add(new FragmentoTexto(null, l));
            }
        } else {
            tipo = "OTRO";
        }

        Documento documento = new Documento();
        documento.setProyecto(proyecto);
        documento.setNombreArchivo(nombreArchivo);
        documento.setTipo(tipo);
        documento.setTamanoBytes(archivo.getSize());
        documento.setHashSha256(hash);
        documento.setEstadoProcesamiento(!fragmentosTexto.isEmpty() ? "INDEXADO" : "CARGADO");
        documento = documentoRepository.save(documento);

        int orden = 1;
        for (FragmentoTexto ft : fragmentosTexto) {
            Fragmento f = new Fragmento();
            f.setDocumento(documento);
            f.setOrden(orden++);
            f.setTexto(ft.texto());
            f.setPagina(ft.pagina());
            fragmentoRepository.save(f);
        }

        if (idUsuario != null) {
            final Documento docFinal = documento;
            usuarioRepository.findById(idUsuario).ifPresent(u ->
                    auditoriaService.registrar(u, "CARGA_DOCUMENTO", "Documento", docFinal.getId()));
        }

        return documento;
    }
}