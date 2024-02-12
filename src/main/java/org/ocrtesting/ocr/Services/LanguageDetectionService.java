package org.ocrtesting.ocr.Services;

import java.io.IOException;

import org.ocrtesting.ocr.Enums.Language;
import org.ocrtesting.ocr.Exceptions.LanguageException;
import org.springframework.stereotype.Service;

@Service
public class LanguageDetectionService {
    public String getLanguageFromRequest(String language) throws LanguageException {

        boolean containsFrench = language.contains("fra") || language.contains("french") || language.contains("fr");
        boolean containsArabic = language.contains("ara") || language.contains("arabe") || language.contains("ar");

        if (containsArabic && containsFrench) return Language.ARABE_LATIN.label;
        if (containsFrench) return Language.FRENCH.label;
        if (containsArabic) return Language.ARABE.label;

        throw new LanguageException("Unsupported Language");
    }

    // Detects Language using the first 2 pages that contains text:
    // public Language detectArabeLanguage() {

    // } 

    // private boolean pdfContainsText() throws IOException {
    // }
}
