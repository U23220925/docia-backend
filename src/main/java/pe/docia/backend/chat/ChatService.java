package pe.docia.backend.chat;

import org.springframework.context.annotation.Lazy; // <-- NUEVO IMPORT IMPRESCINDIBLE
import org.springframework.stereotype.Service;
import pe.docia.backend.auditoria.AuditoriaService;
import pe.docia.backend.auditoria.ConsumoTokens;
import pe.docia.backend.auditoria.ConsumoTokensRepository;
import pe.docia.backend.documento.Fragmento;
import pe.docia.backend.documento.FragmentoRepository;
import pe.docia.backend.proyecto.Proyecto;
import pe.docia.backend.proyecto.ProyectoRepository;
import pe.docia.backend.usuario.Usuario;
import pe.docia.backend.usuario.UsuarioRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ChatService {

    private static final double UMBRAL_SIMILITUD = 0.15;

    private final FragmentoRepository fragmentoRepository;
    private final ProyectoRepository proyectoRepository;
    private final ConversacionRepository conversacionRepository;
    private final MensajeRepository mensajeRepository;
    private final LlmOrchestratorService llmOrchestrator;
    private final ConsumoTokensRepository consumoTokensRepository;
    private final UsuarioRepository usuarioRepository;
    private final AuditoriaService auditoriaService;
    private final ExportacionPdfService exportacionPdfService; 

    // CORRECCIÓN CLAVE: Agregamos @Lazy antes del ExportacionPdfService para romper la dependencia circular de Spring
    public ChatService(FragmentoRepository fragmentoRepository, ProyectoRepository proyectoRepository,
                        ConversacionRepository conversacionRepository, MensajeRepository mensajeRepository,
                        LlmOrchestratorService llmOrchestrator, ConsumoTokensRepository consumoTokensRepository,
                        UsuarioRepository usuarioRepository, AuditoriaService auditoriaService,
                        @Lazy ExportacionPdfService exportacionPdfService) { 
        this.fragmentoRepository = fragmentoRepository;
        this.proyectoRepository = proyectoRepository;
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.llmOrchestrator = llmOrchestrator;
        this.consumoTokensRepository = consumoTokensRepository;
        this.usuarioRepository = usuarioRepository;
        this.auditoriaService = auditoriaService;
        this.exportacionPdfService = exportacionPdfService;
    }

    public RespuestaChat preguntar(Long idProyecto, Long idConversacion, String pregunta, Long idUsuario) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Conversacion conversacion = (idConversacion != null)
                ? conversacionRepository.findById(idConversacion).orElseThrow()
                : conversacionRepository.save(nuevaConversacion(proyecto));

        guardarMensaje(conversacion, "USUARIO", pregunta, true);

        List<Fragmento> fragmentos = fragmentoRepository.findByDocumento_Proyecto_Id(idProyecto);
        List<Fragmento> relevantes = buscarFragmentosRelevantes(pregunta, fragmentos);

        Usuario usuario = idUsuario != null ? usuarioRepository.findById(idUsuario).orElse(null) : null;

        if (relevantes.isEmpty()) {
            String msg = "La consulta no pudo ser corroborada en la documentación cargada para este proyecto.";
            guardarMensaje(conversacion, "ASISTENTE", msg, false);
            return new RespuestaChat(conversacion.getId(), msg, false, List.of());
        }

        String prompt = construirPrompt(pregunta, relevantes);
        LlmRespuesta llmRespuesta = llmOrchestrator.generarRespuesta(prompt);
        guardarMensaje(conversacion, "ASISTENTE", llmRespuesta.getTexto(), true);

        ConsumoTokens consumo = new ConsumoTokens();
        consumo.setProyecto(proyecto);
        consumo.setUsuario(usuario);
        consumo.setModelo(llmRespuesta.getProveedor());
        consumo.setTokensEntrada(llmRespuesta.getTokensEntrada());
        consumo.setTokensSalida(llmRespuesta.getTokensSalida());
        consumoTokensRepository.save(consumo);

        if (usuario != null) {
            auditoriaService.registrar(usuario, "CONSULTA_CHAT", "Conversacion", conversacion.getId());
        }

        List<CitaDTO> citas = relevantes.stream()
                .map(f -> new CitaDTO(f.getId(), f.getDocumento().getNombreArchivo(), f.getPagina(), f.getOrden()))
                .toList();

        return new RespuestaChat(conversacion.getId(), llmRespuesta.getTexto(), true, citas);
    }

    private Conversacion nuevaConversacion(Proyecto proyecto) {
        Conversacion c = new Conversacion();
        c.setProyecto(proyecto);
        return c;
    }

    private List<Fragmento> buscarFragmentosRelevantes(String pregunta, List<Fragmento> fragmentos) {
        Set<String> palabrasPregunta = normalizar(pregunta);
        return fragmentos.stream()
                .map(f -> Map.entry(f, calcularSimilitud(palabrasPregunta, normalizar(f.getTexto()))))
                .filter(e -> e.getValue() >= UMBRAL_SIMILITUD)
                .sorted((a, b) -> Double.compare(b.getValue(), a.getValue()))
                .limit(4)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    private double calcularSimilitud(Set<String> pregunta, Set<String> fragmento) {
        if (pregunta.isEmpty() || fragmento.isEmpty()) return 0.0;
        Set<String> interseccion = new HashSet<>(pregunta);
        interseccion.retainAll(fragmento);
        return (double) interseccion.size() / pregunta.size();
    }

    private Set<String> normalizar(String texto) {
        return Arrays.stream(texto.toLowerCase().split("\\W+"))
                .filter(p -> p.length() > 3)
                .collect(Collectors.toSet());
    }

    private String construirPrompt(String pregunta, List<Fragmento> fragmentos) {
        StringBuilder sb = new StringBuilder();
        sb.append("Eres un asistente experto en ingenieria y construccion. ");
        sb.append("Responde SOLO con base en este contexto. Si no alcanza, dilo.\n\nCONTEXTO:\n");
        for (Fragmento f : fragmentos) {
            sb.append("- [").append(f.getDocumento().getNombreArchivo()).append("] ").append(f.getTexto()).append("\n");
        }
        sb.append("\nPREGUNTA: ").append(pregunta);
        return sb.toString();
    }

    private void guardarMensaje(Conversacion conversacion, String rol, String contenido, boolean corroborado) {
        Mensaje m = new Mensaje();
        m.setConversacion(conversacion);
        m.setRolEmisor(rol);
        m.setContenido(contenido);
        m.setCorroborado(corroborado);
        mensajeRepository.save(m);
    }

    public byte[] exportarConversacionPdf(Long idConversacion) throws Exception {
        Conversacion conversacion = conversacionRepository.findById(idConversacion)
                .orElseThrow(() -> new RuntimeException("Conversacion no encontrada"));
        return exportacionPdfService.exportarConversacion(conversacion);
    }
}
