package org.ocrtesting.ocr.Controllers;

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
    private final OcrService ocrService;

    @Autowired
    private final PdfService pdfService;

    public OcrController(OcrService ocrService, PdfService pdfService) {
        this.ocrService = ocrService;
        this.pdfService = pdfService;
    }


    @PostMapping("/perform")
    public String performOcr(@RequestPart("image") MultipartFile file, @RequestPart("language") String language){
        try {
            if (file.getContentType() != null && file.getContentType().startsWith("image")) {
                // Handle image file
                return ocrService.performOcr(file, language);
            } else if (file.getContentType() != null && file.getContentType().startsWith("application/pdf")) {
                // Handle PDF file
                return pdfService.extractTextFromPdfWithIText(file.getInputStream());
            } else {
                // Unsupported file type
                return "Unsupported file type";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Error processing file";
        }
    }
}
