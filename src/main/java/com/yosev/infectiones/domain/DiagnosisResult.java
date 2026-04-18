package com.yosev.infectiones.domain;

public class DiagnosisResult {
    private final String diseaseName;
    private final double confidence;
    private final String explanation;

    public DiagnosisResult(String diseaseName, double confidence, String explanation) {
        this.diseaseName = diseaseName;
        this.confidence = confidence;
        this.explanation = explanation;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public double getConfidence() {
        return confidence;
    }

    public String getExplanation() {
        return explanation;
    }
}