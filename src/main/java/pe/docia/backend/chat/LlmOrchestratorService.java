package pe.docia.backend.chat;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class LlmOrchestratorService {

    private final List<LlmProvider> proveedores;

    public LlmOrchestratorService(List<LlmProvider> proveedores) {
        this.proveedores = proveedores;
    }

    public LlmRespuesta generarRespuesta(String prompt) {
    Exception ultimoError = null;
    for (LlmProvider proveedor : proveedores) {
        try {
            return proveedor.generarRespuesta(prompt);
        } catch (Exception e) {
            System.out.println("Fallo " + proveedor.getNombre() + ": " + e.getMessage() + " -> probando siguiente");
            ultimoError = e;
        }
    }
    throw new RuntimeException("Todos los proveedores de IA fallaron", ultimoError);
}
}