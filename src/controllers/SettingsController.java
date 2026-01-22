package controllers;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Settings;

public class SettingsController {
    private Settings settings;
    
    public SettingsController(Settings settings) {
        this.settings = settings;
    }
    
    public void showSettingsWindow() {
        Stage settingsStage = new Stage();
        settingsStage.initModality(Modality.APPLICATION_MODAL);
        settingsStage.setTitle("Settings");
        
        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.CENTER_LEFT);
        
        // Voltage
        HBox voltageBox = new HBox(10);
        voltageBox.setAlignment(Pos.CENTER_LEFT);
        Label voltageLabel = new Label("Voltage (V):");
        voltageLabel.setPrefWidth(150);
        TextField voltageField = new TextField(String.valueOf(settings.getVoltage()));
        voltageBox.getChildren().addAll(voltageLabel, voltageField);
        
        // Main Limit
        HBox limitBox = new HBox(10);
        limitBox.setAlignment(Pos.CENTER_LEFT);
        Label limitLabel = new Label("Main Limit (A):");
        limitLabel.setPrefWidth(150);
        TextField limitField = new TextField(String.valueOf(settings.getMainLimit()));
        limitBox.getChildren().addAll(limitLabel, limitField);
        
        // Surge Threshold
        HBox surgeBox = new HBox(10);
        surgeBox.setAlignment(Pos.CENTER_LEFT);
        Label surgeLabel = new Label("Surge Threshold (A):");
        surgeLabel.setPrefWidth(150);
        TextField surgeField = new TextField(String.valueOf(settings.getSurgeThreshold()));
        surgeBox.getChildren().addAll(surgeLabel, surgeField);
        
        // Tariff
        HBox tariffBox = new HBox(10);
        tariffBox.setAlignment(Pos.CENTER_LEFT);
        Label tariffLabel = new Label("Tariff (GHS/kWh):");
        tariffLabel.setPrefWidth(150);
        TextField tariffField = new TextField(String.valueOf(settings.getTariff()));
        tariffBox.getChildren().addAll(tariffLabel, tariffField);
        
        // Simulation Mode
        HBox modeBox = new HBox(10);
        modeBox.setAlignment(Pos.CENTER_LEFT);
        Label modeLabel = new Label("Simulation Mode:");
        modeLabel.setPrefWidth(150);
        ComboBox<String> modeCombo = new ComboBox<>();
        modeCombo.getItems().addAll("Random", "Scripted");
        modeCombo.setValue(settings.getSimulationMode());
        modeBox.getChildren().addAll(modeLabel, modeCombo);
        
        // Buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button saveButton = new Button("Save");
        Button cancelButton = new Button("Cancel");
        
        saveButton.setOnAction(e -> {
            try {
                settings.setVoltage(Double.parseDouble(voltageField.getText()));
                settings.setMainLimit(Double.parseDouble(limitField.getText()));
                settings.setSurgeThreshold(Double.parseDouble(surgeField.getText()));
                settings.setTariff(Double.parseDouble(tariffField.getText()));
                settings.setSimulationMode(modeCombo.getValue());
                settingsStage.close();
            } catch (NumberFormatException ex) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Invalid Input");
                alert.setHeaderText("Please enter valid numbers");
                alert.showAndWait();
            }
        });
        
        cancelButton.setOnAction(e -> settingsStage.close());
        
        buttonBox.getChildren().addAll(saveButton, cancelButton);
        
        layout.getChildren().addAll(
            voltageBox, limitBox, surgeBox, tariffBox, modeBox, buttonBox
        );
        
        Scene scene = new Scene(layout, 400, 300);
        settingsStage.setScene(scene);
        settingsStage.showAndWait();
    }
}
