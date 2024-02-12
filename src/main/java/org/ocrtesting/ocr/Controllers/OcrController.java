package org.ocrtesting.ocr.Controllers;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.ocrtesting.ocr.Enums.PdfExtractionStrategy;
import org.ocrtesting.ocr.Services.FileProcessingService;
import org.ocrtesting.ocr.Services.OcrService;
import org.ocrtesting.ocr.Services.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import java.io.IOException;

@RestController
@RequestMapping("/api/ocr")
public class OcrController {
    @Autowired
    private FileProcessingService fileProcessingService;

    @PostMapping("/perform")
    public String performOcr(@RequestPart("file") MultipartFile file, @RequestPart("language") String language) {
        try {
           return fileProcessingService.processFile(file, language);
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing file";
        }
    }
}
