package pe.docia.backend.documento;

import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocxTextExtractor {

    /** Devuelve el texto del .docx, un elemento de la lista por cada parrafo no vacio. */
    public List<String> extraerParrafos(String rutaArchivo) throws IOException {
        List<String> parrafos = new ArrayList<>();

        try (InputStream entrada = new FileInputStream(rutaArchivo);
             XWPFDocument documento = new XWPFDocument(entrada)) {

            for (XWPFParagraph parrafo : documento.getParagraphs()) {
                String texto = parrafo.getText();
                if (texto != null && !texto.isBlank()) {
                    parrafos.add(texto.trim());
                }
            }
        }
        return parrafos;
    }
}