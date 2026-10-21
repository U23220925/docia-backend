package pe.docia.backend.documento;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.google.common.io.Files;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.Set;

@Service
public class DocumentHashService {

    /** Calcula el SHA-256 de un archivo usando la utilidad de hashing de Guava. */
    public String calcularHashSha256(File archivo) throws IOException {
        HashCode hash = Files.asByteSource(archivo).hash(Hashing.sha256());
        return hash.toString(); // cadena hexadecimal de 64 caracteres
    }

    /** Indica si el documento ya fue cargado antes en el mismo proyecto. */
    public boolean esDuplicado(String hashNuevo, Set<String> hashesExistentes) {
        return hashesExistentes.contains(hashNuevo);
    }
}