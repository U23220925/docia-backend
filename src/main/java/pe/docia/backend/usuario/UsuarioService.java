//(lógica de negocio)
package pe.docia.backend.usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    Usuario crear(Usuario usuario);
    Optional<Usuario> buscarPorId(Long id);
    List<Usuario> listarTodos();
}