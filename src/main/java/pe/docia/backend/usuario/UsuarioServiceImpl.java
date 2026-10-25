package pe.docia.backend.usuario;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pe.docia.backend.auditoria.AuditoriaService;
import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditoriaService auditoriaService;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                              AuditoriaService auditoriaService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditoriaService = auditoriaService;
    }

    @Override
    public Usuario crear(Usuario usuario) {
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        return usuarioRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    @Override
    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario login(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Credenciales incorrectas"));

        if (!passwordEncoder.matches(password, usuario.getPasswordHash())) {
            throw new RuntimeException("Credenciales incorrectas");
        }
        
        auditoriaService.registrar(usuario, "LOGIN", "Usuario", usuario.getId());
        return usuario;
    }

    @Override
    public List<Usuario> listarPorEmpresa(Long idEmpresa) {
        return usuarioRepository.findByEmpresaId(idEmpresa);
    }

    @Override
    public Usuario cambiarEstado(Long id, String nuevoEstado) {
        Usuario u = usuarioRepository.findById(id).orElseThrow();
        u.setEstado(nuevoEstado);
        return usuarioRepository.save(u);
    }

}
