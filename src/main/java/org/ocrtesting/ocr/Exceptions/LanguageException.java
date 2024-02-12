package org.ocrtesting.ocr.Exceptions;

public class LanguageException extends Exception {
    public LanguageException(String message,Throwable error) {
        super(message, error);
    }

    public LanguageException(String message) {
        super(message);
    }
}
