package com.jobportal.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class ResumeParserService {

    // ==============================
    // Step 1 : Save PDF file to server
    // ==============================
    public String saveResume(MultipartFile file) throws IOException {

        // Absolute path — works on Windows without any issues
        String uploadDir = "E:\\job-portal-db\\resumes/";

        // Create folder if it doesn't exist
        File dir = new File(uploadDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Unique file name using timestamp
        String fileName = System.currentTimeMillis()
                          + "_" + file.getOriginalFilename();

        // Full absolute path
        String filePath = uploadDir + fileName;

        // Save file using Files.copy — reliable on Windows
        Files.copy(
            file.getInputStream(),
            Paths.get(filePath),
            StandardCopyOption.REPLACE_EXISTING
        );

        return filePath;
    }

    // ==============================
    // Step 2 : Extract text from PDF
    // ==============================
    public String extractTextFromPDF(String filePath) throws IOException {

        PDDocument document = Loader.loadPDF(new File(filePath));

        PDFTextStripper stripper = new PDFTextStripper();
        String text = stripper.getText(document);

        document.close();

        return text.toLowerCase();
    }
}