package org.ocrtesting.ocr.Services;

import java.io.IOException;

import org.ocrtesting.ocr.Services.FileProcessor.ImageProcessor;
import org.ocrtesting.ocr.Services.FileProcessor.PdfProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class FileProcessingService {
    @Autowired
    private ImageProcessor imageProcessor;

    @Autowired
    private PdfProcessor pdfProcessor;

    public String processFile(MultipartFile file, String language) throws IOException {
        if (file.getContentType() != null && file.getContentType().startsWith("image")) {
            return imageProcessor.processFile(file, language);
        } 
        
        if (file.getContentType() != null && file.getContentType().startsWith("application/pdf")) {
            return pdfProcessor.processFile(file, language);
        } 
        
        return "Unsupported file type";
    }

    
}
