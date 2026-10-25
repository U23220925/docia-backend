package pe.docia.backend.chat;

import org.springframework.stereotype.Service;
import pe.docia.backend.documento.Fragmento;
import pe.docia.backend.documento.FragmentoRepository;
import pe.docia.backend.proyecto.Proyecto;
import pe.docia.backend.proyecto.ProyectoRepository;

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

    public ChatService(FragmentoRepository fragmentoRepository, ProyectoRepository proyectoRepository,
                        ConversacionRepository conversacionRepository, MensajeRepository mensajeRepository,
                        LlmOrchestratorService llmOrchestrator) {
        this.fragmentoRepository = fragmentoRepository;
        this.proyectoRepository = proyectoRepository;
        this.conversacionRepository = conversacionRepository;
        this.mensajeRepository = mensajeRepository;
        this.llmOrchestrator = llmOrchestrator;
    }

    public RespuestaChat preguntar(Long idProyecto, Long idConversacion, String pregunta) {
        Proyecto proyecto = proyectoRepository.findById(idProyecto)
                .orElseThrow(() -> new RuntimeException("Proyecto no encontrado"));

        Conversacion conversacion = (idConversacion != null)
                ? conversacionRepository.findById(idConversacion).orElseThrow()
                : conversacionRepository.save(nuevaConversacion(proyecto));

        guardarMensaje(conversacion, "USUARIO", pregunta, true);

        List<Fragmento> fragmentos = fragmentoRepository.findByDocumento_Proyecto_Id(idProyecto);
        List<Fragmento> relevantes = buscarFragmentosRelevantes(pregunta, fragmentos);

        if (relevantes.isEmpty()) {
            String msg = "La consulta no pudo ser corroborada en la documentación cargada para este proyecto.";
            guardarMensaje(conversacion, "ASISTENTE", msg, false);
            return new RespuestaChat(conversacion.getId(), msg, false, List.of());
        }

        String prompt = construirPrompt(pregunta, relevantes);
        String respuestaIa = llmOrchestrator.generarRespuesta(prompt);
        guardarMensaje(conversacion, "ASISTENTE", respuestaIa, true);

        List<String> citas = relevantes.stream()
                .map(f -> f.getDocumento().getNombreArchivo() + " (fragmento " + f.getOrden() + ")")
                .toList();

        return new RespuestaChat(conversacion.getId(), respuestaIa, true, citas);
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
}