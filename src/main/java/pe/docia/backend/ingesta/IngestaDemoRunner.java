package pe.docia.backend.ingesta;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Set;

@Component
public class IngestaDemoRunner implements CommandLineRunner {

    private final DocumentHashService hashService;
    private final DocxTextExtractor docxExtractor;

    public IngestaDemoRunner(DocumentHashService hashService, DocxTextExtractor docxExtractor) {
        this.hashService = hashService;
        this.docxExtractor = docxExtractor;
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("\n=== DEMO: Google Guava (hash de documento) ===");
        File archivoTxt = new File("samples/Puente_Sur.txt");
        String hash = hashService.calcularHashSha256(archivoTxt);
        System.out.println("Archivo analizado : " + archivoTxt.getName());
        System.out.println("Hash SHA-256       : " + hash);
        boolean duplicado = hashService.esDuplicado(hash, Set.of("c3ab8ff13720e8ad9047dd39466b3c89"));
        System.out.println("Duplicado en el proyecto? " + (duplicado ? "Si" : "No"));

        System.out.println("\n=== DEMO: Apache POI (extraccion de .docx) ===");
        List<String> parrafos = docxExtractor.extraerParrafos("samples/Memoria_Calculo.docx");
        System.out.println("Parrafos extraidos: " + parrafos.size());
        for (int i = 0; i < parrafos.size(); i++) {
            System.out.println("[" + (i + 1) + "] " + parrafos.get(i));
        }
        System.out.println();
    }
}