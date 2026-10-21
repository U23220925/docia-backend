package pe.docia.backend.usuario;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
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
        return usuario;
    }   

}

