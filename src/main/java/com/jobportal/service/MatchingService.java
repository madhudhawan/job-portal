package com.jobportal.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class MatchingService {

    public double calculateScore(String candidateSkills, String requiredSkills) {

        if (requiredSkills == null || requiredSkills.trim().isEmpty()) return 0;
        if (candidateSkills == null || candidateSkills.trim().isEmpty()) return 0;

        // Split AND trim each skill — removes spaces around commas
        List<String> required = Arrays.stream(
            requiredSkills.toLowerCase().split(","))
            .map(String::trim)           // ← removes leading/trailing spaces
            .collect(Collectors.toList());

        List<String> candidate = Arrays.stream(
            candidateSkills.toLowerCase().split(","))
            .map(String::trim)           // ← removes leading/trailing spaces
            .collect(Collectors.toList());

        int matchedCount = 0;
        for (String skill : required) {
            if (candidate.contains(skill)) {
                matchedCount++;
            }
        }

        double score = ((double) matchedCount / required.size()) * 100;
        return Math.round(score * 100.0) / 100.0;
    }
}