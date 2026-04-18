package com.yosev.infectiones;

import com.yosev.infectiones.domain.Category;
import com.yosev.infectiones.domain.DiagnosisResult;
import com.yosev.infectiones.domain.Disease;
import com.yosev.infectiones.domain.SymptomGroup;
import com.yosev.infectiones.engine.RuleBasedEngine;
import com.yosev.infectiones.engine.WeightedEngine;
import com.yosev.infectiones.service.ExpertSystemService;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Main extends Application {

    private final ExpertSystemService service = new ExpertSystemService();
    private final List<CheckBox> allCheckboxes = new ArrayList<>();
    private TextArea logArea;

    private VBox liveResultsBox;
    private TextArea explanationLog;
    private ToggleGroup methodGroup;

    @Override
    public void start(Stage primaryStage) {
        service.loadKnowledgeBase();

        // set app theme
        Application.setUserAgentStylesheet(new atlantafx.base.theme.CupertinoLight().getUserAgentStylesheet());

        // main view layout
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: -color-bg-default;");

        // left pane
        VBox leftPanel = new VBox(20);
        leftPanel.setPadding(new Insets(25));
        leftPanel.setPrefWidth(600);

        Label headerInput = new Label("Pemeriksaan Klinis (Gejala)");
        headerInput.getStyleClass().add("med-header");
        leftPanel.getChildren().add(headerInput);

        VBox symptomsContainer = new VBox(15);
        symptomsContainer.setPadding(new Insets(5));

        for (Category cat : service.getKnowledgeBase()) {
            for (Disease d : cat.getDiseases()) {

                // card for disease and symptomps
                VBox diseaseCard = new VBox(15);
                diseaseCard.getStyleClass().add("med-card");
                diseaseCard.setPadding(new Insets(20));

                Label diseaseLabel = new Label(d.getName().toUpperCase());
                diseaseLabel.getStyleClass().add("med-title");
                diseaseCard.getChildren().add(diseaseLabel);

                GridPane grid = new GridPane();
                grid.setHgap(20);
                grid.setVgap(12);
                int row = 0, col = 0;

                for (SymptomGroup group : d.getSymptomGroups()) {
                    for (String leaf : group.getLeafSymptoms()) {
                        CheckBox checkBox = new CheckBox(leaf);
                        checkBox.setWrapText(true);
                        checkBox.setMaxWidth(260);
                        checkBox.setStyle("-fx-text-fill: #475569;");
                        allCheckboxes.add(checkBox);
                        checkBox.setOnAction(e -> runLiveDiagnosis());

                        grid.add(checkBox, col, row);
                        col++;
                        if (col > 1) { col = 0; row++; }
                    }
                }
                diseaseCard.getChildren().add(grid);
                symptomsContainer.getChildren().add(diseaseCard);
            }
        }

        ScrollPane scrollInput = new ScrollPane(symptomsContainer);
        scrollInput.setFitToWidth(true);
        scrollInput.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: transparent;");
        leftPanel.getChildren().add(scrollInput);
        root.setCenter(leftPanel);


        // right pane
        VBox rightPanel = new VBox(20);
        rightPanel.setPadding(new Insets(25));
        rightPanel.setPrefWidth(450);
        rightPanel.setStyle("-fx-background-color: #ffffff; -fx-border-color: #e2e8f0; -fx-border-width: 0 0 0 1px;");

        Label headerOutput = new Label("Dashboard Diagnosa");
        headerOutput.getStyleClass().add("med-header");

        // algorithm toggle
        HBox methodBox = new HBox(10);
        methodBox.setAlignment(Pos.CENTER_LEFT);
        methodGroup = new ToggleGroup();
        RadioButton rbWeight = new RadioButton("Hierarchical Weighting");
        RadioButton rbRule = new RadioButton("Strict Rule-Based");
        rbWeight.setToggleGroup(methodGroup);
        rbRule.setToggleGroup(methodGroup);
        rbWeight.setSelected(true); // Default

        // listen to algorithm change
        methodGroup.selectedToggleProperty().addListener((obs, oldV, newV) -> runLiveDiagnosis());
        methodBox.getChildren().addAll(rbWeight, rbRule);

        // progress bar box
        liveResultsBox = new VBox(15);

        Label logLabel = new Label("Jejak Inferensi:");
        logLabel.setStyle("-fx-font-weight: bold;");
        explanationLog = new TextArea();
        explanationLog.setEditable(false);
        explanationLog.setWrapText(true);
        explanationLog.setStyle("-fx-font-family: monospace;");
        VBox.setVgrow(explanationLog, Priority.ALWAYS);

        rightPanel.getChildren().addAll(headerOutput, new Separator(), methodBox, new Separator(), liveResultsBox, logLabel, explanationLog);
        root.setRight(rightPanel);

        // initial diagnosis
        runLiveDiagnosis();

        // scene presentation
        Scene scene = new Scene(root, 1280, 800);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/theme.css")).toExternalForm());
        primaryStage.setTitle("Expert System - Clinical Dashboard");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // perform diagnosis
    private void runLiveDiagnosis() {
        // 1. Set Mesin AI
        RadioButton selectedMethod = (RadioButton) methodGroup.getSelectedToggle();
        if (selectedMethod.getText().contains("Strict")) {
            service.setEngine(new RuleBasedEngine());
        } else {
            service.setEngine(new WeightedEngine());
        }

        // collect facts from checkboxes
        List<String> activeSymptoms = new ArrayList<>();
        for (CheckBox checkBox : allCheckboxes) {
            if (checkBox.isSelected()) activeSymptoms.add(checkBox.getText());
        }

        // run diagnosis with preferred inference engine method
        List<DiagnosisResult> results = service.runConsultation(activeSymptoms);

        // update progress bar
        liveResultsBox.getChildren().clear();
        StringBuilder logs = new StringBuilder();

        for (DiagnosisResult res : results) {
            VBox resultBox = new VBox(5);
            HBox titleRow = new HBox();
            Label labelName = new Label(res.getDiseaseName());
            labelName.setStyle("-fx-font-weight: bold;");

            Label labelScore = new Label(String.format("%.1f%%", res.getConfidence()));
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);
            titleRow.getChildren().addAll(labelName, spacer, labelScore);

            ProgressBar progressBar = new ProgressBar(res.getConfidence() / 100.0);
            progressBar.setMaxWidth(Double.MAX_VALUE);

            // dynamic coloring
            if (res.getConfidence() >= 75) {
                progressBar.setStyle("-fx-accent: -color-danger-emphasis;");
                labelScore.setStyle("-fx-text-fill: -color-danger-emphasis; -fx-font-weight: bold;");
            } else if (res.getConfidence() >= 40) {
                progressBar.setStyle("-fx-accent: -color-warning-emphasis;");
            } else {
                progressBar.setStyle("-fx-accent: -color-success-emphasis;");
            }

            resultBox.getChildren().addAll(titleRow, progressBar);
            liveResultsBox.getChildren().add(resultBox);

            // write to trace log
            logs.append(res.getExplanation()).append("\n\n");
        }

        explanationLog.setText(logs.toString());
    }

    public static void main(String[] args) {
        launch(args);
    }
}