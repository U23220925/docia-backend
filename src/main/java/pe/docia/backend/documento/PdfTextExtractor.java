package pe.docia.backend.documento;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Extrae el texto de un PDF, pagina por pagina, usando Apache PDFBox.
 * Para que sirve en DocIA: RF-02 (ingesta de PDF) y RF-03 (chunking),
 * y permite que cada fragmento guarde su numero de pagina real para citar.
 */
@Service
public class PdfTextExtractor {

    public record FragmentoPdf(int pagina, String texto) {}

    public List<FragmentoPdf> extraerPorPagina(File archivo) throws IOException {
        List<FragmentoPdf> resultado = new ArrayList<>();
        try (PDDocument documento = Loader.loadPDF(archivo)) {
            int totalPaginas = documento.getNumberOfPages();
            PDFTextStripper stripper = new PDFTextStripper();
            for (int pagina = 1; pagina <= totalPaginas; pagina++) {
                stripper.setStartPage(pagina);
                stripper.setEndPage(pagina);
                String texto = stripper.getText(documento).trim();
                if (!texto.isBlank()) {
                    resultado.add(new FragmentoPdf(pagina, texto));
                }
            }
        }
        return resultado;
    }
}