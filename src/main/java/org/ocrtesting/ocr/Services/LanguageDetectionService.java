package org.ocrtesting.ocr.Services;

import org.ocrtesting.ocr.Enums.Languages;
import org.ocrtesting.ocr.Exceptions.LanguageException;
import org.springframework.stereotype.Service;

@Service
public class LanguageDetectionService {
    public String getLanguageFromRequest(String language) throws LanguageException {

        boolean containsFrench = language.contains("fra") || language.contains("french") || language.contains("fr");
        boolean containsArabic = language.contains("ara") || language.contains("arabe") || language.contains("ar");

        if (containsFrench) return Languages.FRENCH.label;
        if (containsArabic) return Languages.ARABE.label;
        if (containsArabic && containsFrench) return Languages.ARABE_LATIN.label;

        throw new LanguageException("Unsupported Language");
    }
}
