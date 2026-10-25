package pe.docia.backend.auditoria;

import org.springframework.stereotype.Service;
import pe.docia.backend.usuario.Usuario;

@Service
public class AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    public AuditoriaService(AuditoriaRepository auditoriaRepository) {
        this.auditoriaRepository = auditoriaRepository;
    }

    public void registrar(Usuario usuario, String accion, String entidad, Long idEntidad) {
        Auditoria a = new Auditoria();
        a.setUsuario(usuario);
        a.setAccion(accion);
        a.setEntidad(entidad);
        a.setIdEntidad(idEntidad);
        auditoriaRepository.save(a);
    }
}