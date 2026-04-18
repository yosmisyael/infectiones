package com.yosev.infectiones.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yosev.infectiones.domain.Category;
import com.yosev.infectiones.domain.DiagnosisResult;
import com.yosev.infectiones.domain.Disease;
import com.yosev.infectiones.domain.InferenceEngine;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.lang.reflect.Type;

public class ExpertSystemService {
    private List<Category> knowledgeBase;
    private InferenceEngine engine;

    // load knowledge base defined in json file format
    public void loadKnowledgeBase() {
        try {
            Gson gson = new Gson();
            Type listType = new TypeToken<Map<String, List<Category>>>() {
            }.getType();
            InputStreamReader reader = new InputStreamReader(
                    Objects.requireNonNull(getClass().getResourceAsStream("/knowledge_base.json"))
            );
            Map<String, List<Category>> data = gson.fromJson(reader, listType);
            this.knowledgeBase = data.get("categories");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Not found knowledge base json file");
        }
    }

    public void setEngine(InferenceEngine engine) {
        this.engine = engine;
    }

    public List<Category> getKnowledgeBase() {
        return knowledgeBase;
    }

    public List<DiagnosisResult> runConsultation(List<String> userSymptoms) {
        List<DiagnosisResult> results = new ArrayList<>();
        for (Category cat : knowledgeBase) {
            for (Disease d : cat.getDiseases()) {
                results.add(engine.diagnose(d, userSymptoms));
            }
        }
        results.sort((a, b) -> Double.compare(b.getConfidence(), a.getConfidence()));
        return results;
    }
}
