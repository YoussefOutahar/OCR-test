package org.ocrtesting.ocr.Services;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDTrueTypeFont;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class PdfService {

    public String extractTextFromPdfWithIText(InputStream pdfInputStream) throws IOException {

        StringBuilder pdfTextBuilder = new StringBuilder();

        try {
            PdfReader reader = new PdfReader(pdfInputStream);
            BaseFont arabicBaseFont = BaseFont.createFont("Fonts/Noto_Naskh_Arabic/NotoNaskhArabic-VariableFont_wght.ttf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);

            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                String textFromPage = PdfTextExtractor.getTextFromPage(reader, i);
                pdfTextBuilder.append(new String(textFromPage.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
                pdfTextBuilder.append("\n--- Page Break ---\n");
            }

            return String;
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting text from PDF";
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
    }

    public String extractTextFromPdfWithPdfBox(InputStream pdfInputStream) {
        StringBuilder pdfTextBuilder = new StringBuilder();

        try {
            PDDocument document = PDDocument.load(pdfInputStream);

            PDFont font = PDTrueTypeFont.loadTTF(document, new File("Fonts/Noto_Naskh_Arabic/NotoNaskhArabic-VariableFont_wght.ttf"));
            PDFTextStripper stripper = new PDFTextStripper();
            pdfTextBuilder.append(stripper.getText(document));

            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pdfTextBuilder.toString();
    }
}
