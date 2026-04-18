package com.yosev.infectiones.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Disease {
    private String id;
    private String name;
    @SerializedName("symptom_groups")
    private List<SymptomGroup> symptomGroups;

    public String getId() { return id; }
    public String getName() { return name; }
    public List<SymptomGroup> getSymptomGroups() { return symptomGroups; }

    public int getTotalLeafSymptoms() {
        if (symptomGroups == null) return 0;

        return symptomGroups.stream().mapToInt(symptomGroup -> symptomGroup.getLeafSymptoms().size()).sum();
    }
}