package pe.docia.backend.chat;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public RespuestaChat preguntar(@RequestBody PreguntaRequest request) {
        return chatService.preguntar(request.getIdProyecto(), request.getIdConversacion(),
                request.getPregunta(), request.getIdUsuario());
    }

    @GetMapping("/exportar/{id}")
    public ResponseEntity<byte[]> exportar(@PathVariable Long id) {
        try {
            byte[] pdfBytes = chatService.exportarConversacionPdf(id);
            
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=conversacion.pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdfBytes);
        } catch (Exception e) {
            // Imprimimos el error en consola por si algo falla dentro de PDFBox
            e.printStackTrace(); 
            return ResponseEntity.internalServerError().build();
        }
    }
}
