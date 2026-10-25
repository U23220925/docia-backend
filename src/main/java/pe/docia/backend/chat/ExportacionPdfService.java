package pe.docia.backend.chat;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.springframework.stereotype.Service;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExportacionPdfService {

    @PersistenceContext
    private EntityManager entityManager;

    public byte[] exportarConversacion(Conversacion conversacion) throws IOException {
        List<Mensaje> mensajes = new ArrayList<>();
        try {
            if (conversacion != null) {
                // CORRECCIÓN NATIVA: Usamos una consulta JPQL directa para saltarnos cualquier error de firma en el repositorio
                mensajes = entityManager.createQuery(
                        "SELECT m FROM Mensaje m WHERE m.conversacion.id = :idConversacion ORDER BY m.id ASC", Mensaje.class)
                        .setParameter("idConversacion", conversacion.getId())
                        .getResultList();
            }
        } catch (Exception e) {
            System.err.println("Error critico al recuperar mensajes por query: " + e.getMessage());
        }

        try (PDDocument documento = new PDDocument()) {
            PDPage pagina = new PDPage();
            documento.addPage(pagina);
            
            PDPageContentStream contenido = new PDPageContentStream(documento, pagina);

        // REEMPLAZA LAS LÍNEAS 41 Y 42 EXACTAMENTE POR ESTAS DOS:
        org.apache.pdfbox.pdmodel.font.PDFont fuenteTitulo = new org.apache.pdfbox.pdmodel.font.PDType1Font(org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA_BOLD);
        org.apache.pdfbox.pdmodel.font.PDFont fuenteTexto = new org.apache.pdfbox.pdmodel.font.PDType1Font(org.apache.pdfbox.pdmodel.font.Standard14Fonts.FontName.HELVETICA);


            float y = 750;
            contenido.beginText();
            contenido.setFont(fuenteTitulo, 14);
            contenido.newLineAtOffset(50, y);
            contenido.showText("DocIA - Historial de conversacion #" + (conversacion != null ? conversacion.getId() : "0"));
            contenido.endText();
            y -= 30;

            if (mensajes == null || mensajes.isEmpty()) {
                contenido.beginText();
                contenido.setFont(fuenteTexto, 10);
                contenido.newLineAtOffset(50, y);
                contenido.showText("No se encontraron mensajes registrados en esta conversacion.");
                contenido.endText();
            } else {
                for (Mensaje m : mensajes) {
                    if (m == null || m.getContenido() == null) continue;

                    String etiqueta = "USUARIO".equalsIgnoreCase(m.getRolEmisor()) ? "Pregunta: " : "Respuesta: ";
                    
                    String textoLimpio = (etiqueta + m.getContenido())
                            .replace("**", "").replace("***", "")
                            .replace("á", "a").replace("é", "e").replace("í", "i").replace("ó", "o").replace("ú", "u")
                            .replace("Á", "A").replace("É", "E").replace("Í", "I").replace("Ó", "O").replace("Ú", "U")
                            .replace("ñ", "n").replace("Ñ", "N")
                            .replaceAll("[^\\x20-\\x7E]", "")
                            .trim();

                    for (String linea : partirEnLineas(textoLimpio, 90)) {
                        if (linea.isEmpty()) continue;
                        
                        if (y < 50) {
                            contenido.close();
                            pagina = new PDPage();
                            documento.addPage(pagina);
                            contenido = new PDPageContentStream(documento, pagina);
                            y = 750;
                        }
                        
                        contenido.beginText();
                        contenido.setFont(fuenteTexto, 10);
                        contenido.newLineAtOffset(50, y);
                        try {
                            contenido.showText(linea);
                        } catch (Exception e) {
                            contenido.showText("[Linea omitida]");
                        }
                        contenido.endText();
                        y -= 15;
                    }
                    y -= 10;
                }
            }
            contenido.close();

            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            documento.save(salida);
            return salida.toByteArray();
        }
    }

    private List<String> partirEnLineas(String texto, int maxCaracteres) {
        List<String> lineas = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        for (String palabra : texto.split(" ")) {
            if (palabra.isEmpty()) continue;
            if (actual.length() + palabra.length() + 1 > maxCaracteres) {
                lineas.add(actual.toString());
                actual = new StringBuilder();
            }
            if (actual.length() > 0) actual.append(" ");
            actual.append(palabra);
        }
        if (actual.length() > 0) lineas.add(actual.toString());
        return lineas;
    }
}
