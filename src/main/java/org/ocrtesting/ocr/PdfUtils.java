package org.ocrtesting.ocr;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfUtils {
    //! Note this only work for the first Page !!!
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
