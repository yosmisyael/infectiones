package com.yosev.infectiones.engine;
import com.yosev.infectiones.domain.DiagnosisResult;
import com.yosev.infectiones.domain.Disease;
import com.yosev.infectiones.domain.InferenceEngine;
import com.yosev.infectiones.domain.SymptomGroup;

import java.util.List;

public class WeightedEngine implements InferenceEngine {
    @Override
    public DiagnosisResult diagnose(Disease disease, List<String> userSymptoms) {
        int groupCount = disease.getSymptomGroups().size();
        double weightPerGroup = 100.0 / groupCount;

        double totalConfidence = 0.0;
        StringBuilder explanation = new StringBuilder(String.format("[HIERARCHICAL WEIGHTING] Evaluasi %s:\n", disease.getName()));
        explanation.append(String.format("Ada %d Cabang Gejala Spesifik (Masing-masing bernilai %.2f%%)\n", groupCount, weightPerGroup));

        for (SymptomGroup group : disease.getSymptomGroups()) {
            int terminalNode = group.getLeafSymptoms().size();
            long matchCount = group.getLeafSymptoms().stream().filter(userSymptoms::contains).count();

            // leaf node calculation
            double localPercentage = (double) matchCount / terminalNode;
            // calculate global percentage contribution
            double globalContribution = localPercentage * weightPerGroup;

            totalConfidence += globalContribution;

            explanation.append(String.format(" - %s: %d/%d gejala spesifik (terminal node) cocok -> Menyumbang %.2f%%\n",
                    group.getName(), matchCount, terminalNode, globalContribution));
        }

        explanation.append(String.format("Total Kecocokan: %.2f%%", totalConfidence));
        return new DiagnosisResult(disease.getName(), totalConfidence, explanation.toString());
    }
}
