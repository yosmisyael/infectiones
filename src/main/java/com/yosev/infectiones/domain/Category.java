package com.yosev.infectiones.domain;
import java.util.List;

public class Category {
    private String name;
    private List<Disease> diseases;

    public String getName() { return name; }
    public List<Disease> getDiseases() { return diseases; }
}
