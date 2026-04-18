package com.yosev.infectiones.domain;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SymptomGroup {
    private String name;

    @SerializedName("leaf_symptoms")
    private List<String> leafSymptoms;

    public String getName() {
        return name;
    }

    public List<String> getLeafSymptoms() {
        return leafSymptoms == null ? List.of() : leafSymptoms;
    }
}
