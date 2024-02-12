package org.ocrtesting.ocr.Services.FileProcessor;

import java.io.IOException;

import org.ocrtesting.ocr.Services.OcrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;

public class ImageProcessor implements FileProcessor {

    @Autowired
    private OcrService ocrService;

    @Override
    public String processFile(MultipartFile file, String language) throws IOException {
        return ocrService.performOCR(file, language);
    }
    
}
