package pe.docia.backend.proyecto;

import java.util.List;

public interface ProyectoService {
    Proyecto crear(Proyecto proyecto);
    List<Proyecto> listarTodos();
    List<Proyecto> listarPorEmpresa(Long idEmpresa);
}