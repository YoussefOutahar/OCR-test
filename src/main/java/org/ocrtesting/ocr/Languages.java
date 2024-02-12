package org.ocrtesting.ocr;

public enum Languages {
    ARABE("ara"),
    FRENCH("fra"),
    ENGLISH("eng"),
    ARABE_LATIN("eng+ara");

    public final String label;

    private Languages(String label) {
        this.label = label;
    }
    
}