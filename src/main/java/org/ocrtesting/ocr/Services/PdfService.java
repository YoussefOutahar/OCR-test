package org.ocrtesting.ocr.Services;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javax.imageio.ImageIO;

import java.io.ByteArrayOutputStream;
import org.springframework.mock.web.MockMultipartFile;
import java.io.IOException;

@Service
public class PdfService {

    @Autowired
    private OcrService ocrService;

    public String extractTextFromPdfWithIText(InputStream pdfInputStream) throws IOException {

        StringBuilder pdfTextBuilder = new StringBuilder();

        try {
            PdfReader reader = new PdfReader(pdfInputStream);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            TextExtractionStrategy strategy;

            for (int i = 1; i <= reader.getNumberOfPages(); i++) {

                strategy = parser.processContent(i, new LocationTextExtractionStrategy());
                

                pdfTextBuilder.append(strategy.getResultantText());
                pdfTextBuilder.append("\n--- Page Break ---\n");
            }

            reader.close();

            return pdfTextBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting text from PDF";
        }
    }

    public String extractTextFromPdfWithPdfBox(InputStream pdfInputStream) {
        StringBuilder pdfTextBuilder = new StringBuilder();

        try {
            PDDocument document = PDDocument.load(pdfInputStream);

            // PDFont font = PDTrueTypeFont.loadTTF(document, new File("Fonts/Noto_Naskh_Arabic/NotoNaskhArabic-VariableFont_wght.ttf"));
            PDFTextStripper stripper = new PDFTextStripper();
            pdfTextBuilder.append(stripper.getText(document));

            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pdfTextBuilder.toString();
    }

    public String extractTextFromPdfWithImageTransform(InputStream pdfInputStream) throws IOException {
        BufferedImage image = pdfToImage(pdfInputStream);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        return ocrService.performOcr(new MockMultipartFile("ImageFromPDF.png", imageInByte), "eng+ara");
    }

    public BufferedImage pdfToImage(InputStream pdfInputStream) throws IOException {
        try (PDDocument document = PDDocument.load(pdfInputStream)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            return pdfRenderer.renderImageWithDPI(0, 300); // 0 is the page number, 300 is the DPI
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
