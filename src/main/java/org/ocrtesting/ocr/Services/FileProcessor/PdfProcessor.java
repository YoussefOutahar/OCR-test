package org.ocrtesting.ocr.Services.FileProcessor;

import java.io.IOException;

import org.ocrtesting.ocr.Enums.PdfExtractionStrategy;
import org.ocrtesting.ocr.Services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class PdfProcessor implements FileProcessor{

    @Autowired
    private PdfService pdfService;

    @Override
    public String processFile(MultipartFile file, String language) throws IOException {
        return pdfService.extractTextFromPdf(file.getInputStream(), PdfExtractionStrategy.ADAPTIVE);
    }
    
}
