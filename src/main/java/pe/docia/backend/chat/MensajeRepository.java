package pe.docia.backend.chat;
import org.springframework.data.jpa.repository.JpaRepository;
public interface MensajeRepository extends JpaRepository<Mensaje, Long> {}