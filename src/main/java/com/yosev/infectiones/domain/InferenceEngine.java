package com.yosev.infectiones.domain;

import java.util.List;

public interface InferenceEngine {
    DiagnosisResult diagnose(Disease disease, List<String> userSymptoms);
}
