//(API REST — la V del MVC hacia afuera)
package pe.docia.backend.usuario;

import org.springframework.web.bind.annotation.*;
import pe.docia.backend.empresa.Empresa;
import pe.docia.backend.empresa.EmpresaRepository;
import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final EmpresaRepository empresaRepository; // Nuevo campo agregado

    // Constructor actualizado para inyectar EmpresaRepository
    public UsuarioController(UsuarioService usuarioService, EmpresaRepository empresaRepository) {
        this.usuarioService = usuarioService;
        this.empresaRepository = empresaRepository;
    }

    // Método crear actualizado utilizando UsuarioRequest y asociando la Empresa
    @PostMapping
    public Usuario crear(@RequestBody UsuarioRequest request) {
        Usuario usuario = new Usuario();
        usuario.setNombre(request.getNombre());
        usuario.setCorreo(request.getCorreo());
        usuario.setPasswordHash(request.getPassword()); // El Service se encargará de encriptarla
        usuario.setRol(request.getRol());

        if (request.getIdEmpresa() != null) {
            Empresa empresa = empresaRepository.findById(request.getIdEmpresa())
                    .orElseThrow(() -> new RuntimeException("Empresa no encontrada"));
            usuario.setEmpresa(empresa);
        }

        return usuarioService.crear(usuario);
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody LoginRequest request) {
        return usuarioService.login(request.getCorreo(), request.getPassword());
    }

    @GetMapping
    public List<Usuario> listar(@RequestParam(required = false) Long idEmpresa) {
        return idEmpresa != null ? usuarioService.listarPorEmpresa(idEmpresa) : usuarioService.listarTodos();
    }

    @PutMapping("/{id}/estado")
    public Usuario cambiarEstado(@PathVariable Long id, @RequestBody java.util.Map<String, String> body) {
        return usuarioService.cambiarEstado(id, body.get("estado"));
    }
}
