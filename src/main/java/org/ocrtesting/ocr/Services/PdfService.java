package org.ocrtesting.ocr.Services;

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

        if (PdfExtractionStrategy.ADAPTIVE.equals(strategy)) {
            output = adaptiveExtract(pdfInputStream);
        }

        if (strategy == PdfExtractionStrategy.NORMAL_EXTRACTION) {
            output = extractTextFromPdfWithPdfBox(pdfInputStream);
        }

        if (strategy == PdfExtractionStrategy.TRANSFORM_TO_IMAGE) {
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

            // For normal extraction
            PDFTextStripper pdfStripper = new PDFTextStripper();

            // For OCR text extraction
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            for (int pageIndex = 0; pageIndex < document.getNumberOfPages(); pageIndex++) {
                pdfStripper.setStartPage(pageIndex);
                pdfStripper.setEndPage(pageIndex);
                String resultantString = pdfStripper.getText(document);

                boolean isMalExtracted = languageDetectionService.isProbablyMalExtracted(resultantString);

                if (isMalExtracted) {
                    resultantString = getImageInByte(pdfRenderer, pageIndex);
                } 
                pdfTextBuilder.append(resultantString);
            }
            return pdfTextBuilder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting text from PDF";
        }
    }

    private String getImageInByte(PDFRenderer pdfRenderer, int pageIndex) throws IOException {
        String resultantString;
        BufferedImage image = pdfRenderer.renderImageWithDPI(pageIndex, 300);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        byte[] imageInByte = baos.toByteArray();
        baos.close();

        resultantString = ocrService.performOCR(new MockMultipartFile("PDF.png", imageInByte),
                Language.ARABE_LATIN.label);
        return resultantString;
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
        } catch (IOException e) {
            e.printStackTrace();
            return "Error extracting text from PDF";
        }
    }

    private String extractTextFromPdfWithPdfBox(InputStream pdfInputStream) {
        StringBuilder pdfTextBuilder = new StringBuilder();

        try {
            PDDocument document = PDDocument.load(pdfInputStream);

            // PDFont font = PDTrueTypeFont.loadTTF(document, new
            // File("Fonts/Noto_Naskh_Arabic/NotoNaskhArabic-VariableFont_wght.ttf"));
            PDFTextStripper stripper = new PDFTextStripper();
            pdfTextBuilder.append(stripper.getText(document));

            document.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return pdfTextBuilder.toString();
    }
}
