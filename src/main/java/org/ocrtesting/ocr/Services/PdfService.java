package org.ocrtesting.ocr.Services;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.LocationTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.ocrtesting.ocr.Enums.Language;
import org.ocrtesting.ocr.Enums.PdfExtractionStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    @Autowired
    private LanguageDetectionService languageDetectionService;

    public String extractTextFromPdf(InputStream pdfInputStream, PdfExtractionStrategy strategy) throws IOException {
        String output = "";

        if (strategy == PdfExtractionStrategy.ADAPTIVE) {
            System.out.println("Performing Adaptive pdf extraction");
            output = adaptiveExtract(pdfInputStream);
        }

        if (strategy == PdfExtractionStrategy.NORMAL_EXTRACTION) {
            System.out.println("Performing Normal pdf extraction");
            output = extractTextFromPdfWithIText(pdfInputStream);
        }

        if (strategy == PdfExtractionStrategy.TRANSFORM_TO_IMAGE) {
            System.out.println("Performing Pdf transformation to image");
            output = extractTextFromPdfWithImageTransform(pdfInputStream);
        }

        if (strategy == PdfExtractionStrategy.IGNORE_PDF) {
            throw new IOException();
        }

        return output;
    }

    private String adaptiveExtract(InputStream pdfInputStream) throws IOException {
        StringBuilder pdfTextBuilder = new StringBuilder();

        try (PDDocument document = PDDocument.load(pdfInputStream)) {

            // For Normal text extraction
            PdfReader reader = new PdfReader(pdfInputStream);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            TextExtractionStrategy strategy;

            // For OCR text extraction
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int pageIndex = 1; pageIndex <= reader.getNumberOfPages(); pageIndex++) {

                strategy = parser.processContent(pageIndex, new LocationTextExtractionStrategy());
                String resultantString = strategy.getResultantText();

                boolean isMalExtracted = languageDetectionService.isProbablyMalExtracted(resultantString);

                if (isMalExtracted) {
                    System.out.println("detected Mal extraction at page " + pageIndex );
                    BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(image, "png", baos);
                    byte[] imageInByte = baos.toByteArray();
                    baos.close();

                    String result = ocrService.performOCR(new MockMultipartFile("ImageFromPDF.png", imageInByte),
                            Language.ARABE_LATIN.label);
                    pdfTextBuilder.append(result);
                } else {
                    System.out.println("extracting normaly at page " + pageIndex);
                    pdfTextBuilder.append(resultantString);
                }
            }
            reader.close();
            return pdfTextBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting text from PDF";
        }
    }

    private String extractTextFromPdfWithImageTransform(InputStream pdfInputStream) throws IOException {
        StringBuilder allPagesText = new StringBuilder();

        try (PDDocument document = PDDocument.load(pdfInputStream)) {
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage image = pdfRenderer.renderImageWithDPI(page, 300);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(image, "png", baos);
                byte[] imageInByte = baos.toByteArray();
                baos.close();

                String result = ocrService.performOCR(new MockMultipartFile("ImageFromPDF.png", imageInByte),
                        Language.ARABE_LATIN.label);
                allPagesText.append(result);
            }

            return allPagesText.toString();
        }
    }

    private String extractTextFromPdfWithIText(InputStream pdfInputStream) throws IOException {
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

    // private String extractTextFromPdfWithPdfBox(InputStream pdfInputStream) {
    //     StringBuilder pdfTextBuilder = new StringBuilder();

    //     try {
    //         PDDocument document = PDDocument.load(pdfInputStream);

    //         // PDFont font = PDTrueTypeFont.loadTTF(document, new
    //         // File("Fonts/Noto_Naskh_Arabic/NotoNaskhArabic-VariableFont_wght.ttf"));
    //         PDFTextStripper stripper = new PDFTextStripper();
    //         pdfTextBuilder.append(stripper.getText(document));

    //         document.close();
    //     } catch (IOException e) {
    //         throw new RuntimeException(e);
    //     }

    //     return pdfTextBuilder.toString();
    // }
}
