package pe.docia.backend.auditoria;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ConsumoTokensRepository extends JpaRepository<ConsumoTokens, Long> {
    List<ConsumoTokens> findByProyectoId(Long idProyecto);
}