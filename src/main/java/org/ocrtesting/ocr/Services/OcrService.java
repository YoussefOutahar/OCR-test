package org.ocrtesting.ocr.Services;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import org.ocrtesting.ocr.Exceptions.LanguageException;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Service
public class OcrService {

    static {
        System.loadLibrary(org.opencv.core.Core.NATIVE_LIBRARY_NAME);
    }

    @Autowired
    private LanguageDetectionService languageDetectionService;

    public String performOCR(MultipartFile imageFile, String language) throws IOException {
        try {
            byte[] bytes = imageFile.getBytes();
            InputStream is = new ByteArrayInputStream(bytes);
            BufferedImage bufferedImage = ImageIO.read(is);

            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:/Users/Root/Desktop/OCR-test/src/main/resources/OCR/tessdata");
            tesseract.setLanguage(languageDetectionService.getLanguageFromRequest(language));
            return tesseract.doOCR(bufferedImage);
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            return "Error performing OCR";
        } catch (LanguageException e) {
            e.printStackTrace();
            return "Unsupported Language";
        }

    }

    public String performOcrWithOptimisation(MultipartFile imageFile, String language) throws IOException {
        try {
            // Load image using OpenCV
            byte[] bytes = imageFile.getBytes();
            Mat image = Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_COLOR);

            // Convert image to grayscale
            Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2GRAY);

            // Apply GaussianBlur to reduce noise
            Imgproc.GaussianBlur(image, image, new Size(5, 5), 0);

            // Perform thresholding to enhance text
            Imgproc.threshold(image, image, 0, 255, Imgproc.THRESH_BINARY + Imgproc.THRESH_OTSU);

            // Save the preprocessed image for debugging (optional)
            Imgcodecs.imwrite("preprocessed_image.jpg", image);

            // Convert OpenCV Mat to BufferedImage
            MatOfByte matOfByte = new MatOfByte();
            Imgcodecs.imencode(".jpg", image, matOfByte);
            byte[] byteArray = matOfByte.toArray();
            BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(byteArray));

            // Perform OCR on the preprocessed image
            ITesseract tesseract = new Tesseract();
            tesseract.setDatapath("C:/Users/Root/Desktop/OCR-test/src/main/resources/OCR/tessdata");
            tesseract.setTessVariable("dpi", "300");
            tesseract.setLanguage(language);
            return tesseract.doOCR(bufferedImage);
        } catch (IOException | TesseractException e) {
            e.printStackTrace();
            return "Error performing OCR";
        }
    }
}
