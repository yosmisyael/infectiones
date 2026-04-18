package com.yosev.infectiones.engine;

import com.yosev.infectiones.domain.DiagnosisResult;
import com.yosev.infectiones.domain.Disease;
import com.yosev.infectiones.domain.InferenceEngine;
import com.yosev.infectiones.domain.SymptomGroup;

import java.util.List;

public class RuleBasedEngine implements InferenceEngine {
    @Override
    public DiagnosisResult diagnose(Disease disease, List<String> userSymptoms) {
        long totalMatch = 0;
        for (SymptomGroup group : disease.getSymptomGroups()) {
            totalMatch += group.getLeafSymptoms().stream().filter(userSymptoms::contains).count();
        }

        int totalRequired = disease.getTotalLeafSymptoms();
        boolean isMatch = totalMatch == totalRequired;

        String explanation = String.format("[RULE-BASED] Evaluasi %s:\nSyarat Mutlak: %d fakta gejala (terminal node). Terpenuhi: %d daun.\nKesimpulan: %s",
                disease.getName(), totalRequired, totalMatch, (isMatch ? "TERDIAGNOSA" : "GUGUR"));

        return new DiagnosisResult(disease.getName(), isMatch ? 100.0 : 0.0, explanation);
    }
}
