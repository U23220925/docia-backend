package pe.docia.backend.documento;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FragmentoRepository extends JpaRepository<Fragmento, Long> {
    List<Fragmento> findByDocumento_Proyecto_Id(Long idProyecto);
}