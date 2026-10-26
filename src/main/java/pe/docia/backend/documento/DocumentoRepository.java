package pe.docia.backend.documento;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DocumentoRepository extends JpaRepository<Documento, Long> {
    Optional<Documento> findByHashSha256(String hashSha256);
    long countByProyectoId(Long idProyecto);
    List<Documento> findByProyectoId(Long idProyecto);
}