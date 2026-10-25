package pe.docia.backend.chat;

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
        return chatService.preguntar(request.getIdProyecto(), request.getIdConversacion(), request.getPregunta());
    }
}