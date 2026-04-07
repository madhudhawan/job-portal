package com.jobportal.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

@Service
public class SkillExtractorService {

    private static final List<String> KNOWN_SKILLS = Arrays.asList(
        // Java technologies
        "java", "spring boot", "spring", "spring mvc",
        "hibernate", "jdbc", "jsp", "servlets", "maven", "gradle",

        // Database
        "mysql", "sql", "postgresql", "mongodb", "oracle", "redis",

        // Web
        "html", "css", "javascript", "bootstrap", "jquery",

        // Frontend
        "react", "angular", "vue",

        // Backend others
        "python", "django", "flask", "node.js", "php",

        // API
        "rest api", "restful", "rest", "microservices",

        // Tools
        "aws", "docker", "kubernetes", "git", "github", "linux",

        // Testing
        "junit", "postman"
    );

    public String extractSkills(String resumeText) {

        if (resumeText == null || resumeText.isEmpty()) {
            return "";
        }

        // Clean the resume text
        // Replace hyphens with spaces — "spring-boot" becomes "spring boot"
        String cleanedText = resumeText
            .toLowerCase()
            .replace("-", " ")       // spring-boot → spring boot
            .replace("\n", " ")      // remove new lines
            .replace("\r", " ")      // remove carriage returns
            .replaceAll("\\s+", " "); // multiple spaces → single space

        List<String> foundSkills = new ArrayList<>();

        for (String skill : KNOWN_SKILLS) {
            // Check if skill exists as a word — not inside another word
            // Example : "javascript" should not match when looking for "java"
            if (containsSkill(cleanedText, skill)) {
                foundSkills.add(skill);
            }
        }

        return String.join(",", foundSkills);
    }

    // ==============================
    // Smart skill matching
    // Checks skill as whole word — not partial match
    // ==============================
    private boolean containsSkill(String text, String skill) {

        // For multi-word skills like "spring boot", "rest api"
        // just use contains — word boundary doesn't apply
        if (skill.contains(" ")) {
            return text.contains(skill);
        }

        // For single word skills like "java", "mysql"
        // use word boundary so "java" doesn't match "javascript"
        // \b means word boundary in regex
        String pattern = "\\b" + skill + "\\b";
        return text.matches(".*" + pattern + ".*");
    }
}