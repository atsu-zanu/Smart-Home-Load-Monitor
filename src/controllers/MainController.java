package controllers;

import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import models.*;
import simulation.*;
import utils.StatusCalculator;

import java.util.*;

public class MainController {
    private List<Appliance> appliances;
    private Map<String, SocketGroup> socketGroups;
    private Settings settings;
    private SimulationEngine simulationEngine;
    private List<models.Alert> alerts;
    private AnimationTimer timer;
    private boolean isRunning = false;
    
    private TableView<Appliance> applianceTable;
    private ListView<String> alertList;
    private ListView<String> recommendationList;
    private Label totalCurrentLabel;
    private Label powerLabel;
    private Label statusLabel;
    private Label energyLabel;
    private Label costLabel;
    private VBox socketGroupPanel;
    
    private double totalEnergy = 0.0; // in kWh
    private long lastUpdateTime = 0;
    
    public MainController() {
        settings = new Settings();
        appliances = new ArrayList<>();
        socketGroups = new HashMap<>();
        alerts = new ArrayList<>();
        initializeAppliances();
        simulationEngine = new RandomSimulation();
    }
    
    private void initializeAppliances() {
        // Kitchen appliances
        appliances.add(new Appliance("Refrigerator", "Kitchen", "Kitchen", "Essential", 5.0));
        appliances.add(new Appliance("Microwave", "Kitchen", "Kitchen", "Non-essential", 8.0));
        appliances.add(new Appliance("Electric Kettle", "Kitchen", "Kitchen", "Non-essential", 13.0));
        
        // Living Room appliances
        appliances.add(new Appliance("TV", "Living Room", "Living Room", "Non-essential", 3.0));
        appliances.add(new Appliance("Air Conditioner", "Living Room", "Living Room", "Non-essential", 12.0));
        appliances.add(new Appliance("Fan", "Living Room", "Living Room", "Non-essential", 2.0));
        
        // Bedroom appliances
        appliances.add(new Appliance("Laptop", "Bedroom", "Bedroom", "Essential", 2.0));
        appliances.add(new Appliance("Iron", "Bedroom", "Bedroom", "Non-essential", 12.0));
        
        // Bathroom appliances
        appliances.add(new Appliance("Water Heater", "Bathroom", "Bathroom", "Essential", 13.0));
        appliances.add(new Appliance("Washing Machine", "Bathroom", "Bathroom", "Non-essential", 10.0));
        
        // Group appliances
        for (Appliance app : appliances) {
            String groupName = app.getSocketGroup();
            socketGroups.putIfAbsent(groupName, new SocketGroup(groupName));
            socketGroups.get(groupName).addAppliance(app);
        }
    }
    
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Smart Home Load Monitor");
        
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        
        // Top toolbar
        HBox toolbar = createToolbar(primaryStage);
        root.setTop(toolbar);
        
        // Center - Main content
        SplitPane mainContent = new SplitPane();
        mainContent.getItems().addAll(createLeftPanel(), createRightPanel());
        mainContent.setDividerPositions(0.6);
        root.setCenter(mainContent);
        
        // Bottom - Alerts and Recommendations
        TabPane bottomPanel = createBottomPanel();
        root.setBottom(bottomPanel);
        
        Scene scene = new Scene(root, 1200, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private HBox createToolbar(Stage stage) {
        HBox toolbar = new HBox(15);
        toolbar.setPadding(new Insets(10));
        toolbar.setStyle("-fx-background-color: #34495e;");
        
        Label titleLabel = new Label("Smart Home Load Monitor");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold;");
        
        Button startButton = new Button("Start");
        Button stopButton = new Button("Stop");
        Button settingsButton = new Button("Settings");
        
        startButton.setOnAction(e -> startSimulation());
        stopButton.setOnAction(e -> stopSimulation());
        settingsButton.setOnAction(e -> {
            stopSimulation();
            SettingsController settingsController = new SettingsController(settings);
            settingsController.showSettingsWindow();
            updateSimulationEngine();
        });
        
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);
        
        toolbar.getChildren().addAll(titleLabel, spacer, startButton, stopButton, settingsButton);
        return toolbar;
    }
    
    private VBox createLeftPanel() {
        VBox leftPanel = new VBox(10);
        leftPanel.setPadding(new Insets(10));
        
        Label tableTitle = new Label("Appliance Monitor");
        tableTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        applianceTable = new TableView<>();
        applianceTable.setItems(javafx.collections.FXCollections.observableArrayList(appliances));
        
        TableColumn<Appliance, String> nameCol = new TableColumn<>("Appliance");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        nameCol.setPrefWidth(120);
        
        TableColumn<Appliance, String> locationCol = new TableColumn<>("Location");
        locationCol.setCellValueFactory(new PropertyValueFactory<>("location"));
        locationCol.setPrefWidth(100);
        
        TableColumn<Appliance, String> groupCol = new TableColumn<>("Socket Group");
        groupCol.setCellValueFactory(new PropertyValueFactory<>("socketGroup"));
        groupCol.setPrefWidth(100);
        
        TableColumn<Appliance, Double> currentCol = new TableColumn<>("Current (A)");
        currentCol.setCellValueFactory(new PropertyValueFactory<>("currentAmps"));
        currentCol.setPrefWidth(80);
        currentCol.setCellFactory(column -> new TableCell<Appliance, Double>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(String.format("%.2f", item));
                }
            }
        });
        
        TableColumn<Appliance, String> priorityCol = new TableColumn<>("Priority");
        priorityCol.setCellValueFactory(new PropertyValueFactory<>("priority"));
        priorityCol.setPrefWidth(100);
        
        TableColumn<Appliance, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setPrefWidth(80);
        statusCol.setCellFactory(column -> new TableCell<Appliance, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    switch (item) {
                        case "OK":
                            setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white;");
                            break;
                        case "WARNING":
                            setStyle("-fx-background-color: #f39c12; -fx-text-fill: white;");
                            break;
                        case "DANGER":
                            setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                            break;
                        case "INVALID":
                            setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white;");
                            break;
                        case "SURGE":
                            setStyle("-fx-background-color: #9b59b6; -fx-text-fill: white;");
                            break;
                    }
                }
            }
        });
        
        applianceTable.getColumns().addAll(nameCol, locationCol, groupCol, currentCol, priorityCol, statusCol);
        
        leftPanel.getChildren().addAll(tableTitle, applianceTable);
        VBox.setVgrow(applianceTable, Priority.ALWAYS);
        
        return leftPanel;
    }
    
    private VBox createRightPanel() {
        VBox rightPanel = new VBox(15);
        rightPanel.setPadding(new Insets(10));
        rightPanel.setPrefWidth(400);
        
        // Summary Card
        VBox summaryCard = new VBox(10);
        summaryCard.setPadding(new Insets(15));
        summaryCard.setStyle("-fx-background-color: #ecf0f1; -fx-background-radius: 5;");
        
        Label summaryTitle = new Label("System Summary");
        summaryTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        totalCurrentLabel = new Label("Total Current: 0.00 A");
        powerLabel = new Label("Estimated Power: 0.00 W");
        energyLabel = new Label("Session Energy: 0.000 kWh");
        costLabel = new Label("Session Cost: GHS 0.00");
        statusLabel = new Label("Status: OK");
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        Label mainLimitLabel = new Label("Main Limit: " + settings.getMainLimit() + " A");
        
        summaryCard.getChildren().addAll(
            summaryTitle, totalCurrentLabel, powerLabel, 
            energyLabel, costLabel, mainLimitLabel, statusLabel
        );
        
        // Socket Groups Panel
        Label socketTitle = new Label("Socket Groups");
        socketTitle.setStyle("-fx-font-size: 14px; -fx-font-weight: bold;");
        
        socketGroupPanel = new VBox(10);
        updateSocketGroupPanel();
        
        ScrollPane socketScroll = new ScrollPane(socketGroupPanel);
        socketScroll.setFitToWidth(true);
        socketScroll.setPrefHeight(250);
        
        rightPanel.getChildren().addAll(summaryCard, socketTitle, socketScroll);
        
        return rightPanel;
    }
    
    private TabPane createBottomPanel() {
        TabPane tabPane = new TabPane();
        tabPane.setPrefHeight(200);
        
        // Alerts Tab
        Tab alertTab = new Tab("Alerts");
        alertTab.setClosable(false);
        alertList = new ListView<>();
        alertList.setStyle("-fx-font-family: monospace;");
        alertTab.setContent(alertList);
        
        // Recommendations Tab
        Tab recTab = new Tab("Load Shedding Recommendations");
        recTab.setClosable(false);
        recommendationList = new ListView<>();
        recTab.setContent(recommendationList);
        
        tabPane.getTabs().addAll(alertTab, recTab);
        
        return tabPane;
    }
    
    private void updateSocketGroupPanel() {
        socketGroupPanel.getChildren().clear();
        
        for (SocketGroup group : socketGroups.values()) {
            VBox groupBox = new VBox(5);
            groupBox.setPadding(new Insets(10));
            
            double groupCurrent = group.getTotalCurrent();
            String status = group.getStatus();
            
            String bgColor = "#2ecc71";
            switch (status) {
                case "WARNING": bgColor = "#f39c12"; break;
                case "DANGER": bgColor = "#e74c3c"; break;
            }
            
            groupBox.setStyle("-fx-background-color: " + bgColor + 
                            "; -fx-background-radius: 5; -fx-text-fill: white;");
            
            Label groupName = new Label(group.getName());
            groupName.setStyle("-fx-font-weight: bold; -fx-text-fill: white;");
            
            Label groupCurrentLabel = new Label(String.format("%.2f A / 13.00 A", groupCurrent));
            groupCurrentLabel.setStyle("-fx-text-fill: white;");
            
            Label groupStatus = new Label("Status: " + status);
            groupStatus.setStyle("-fx-text-fill: white;");
            
            groupBox.getChildren().addAll(groupName, groupCurrentLabel, groupStatus);
            socketGroupPanel.getChildren().add(groupBox);
        }
    }
    
    private void startSimulation() {
        if (isRunning) return;
        
        isRunning = true;
        totalEnergy = 0.0;
        lastUpdateTime = System.nanoTime();
        alerts.clear();
        alertList.getItems().clear();
        
        timer = new AnimationTimer() {
            private long lastUpdate = 0;
            
            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 1_000_000_000) { // Update every 1 second
                    updateSimulation();
                    lastUpdate = now;
                }
            }
        };
        timer.start();
    }
    
    private void stopSimulation() {
        if (!isRunning) return;
        
        isRunning = false;
        if (timer != null) {
            timer.stop();
        }
        simulationEngine.reset();
    }
    
    private void updateSimulation() {
        // Update sensor readings
        simulationEngine.updateReadings(appliances);
        
        // Clear previous alerts for this cycle
        List<models.Alert> currentAlerts = new ArrayList<>();
        
        // Validate readings
        currentAlerts.addAll(StatusCalculator.validateReadings(appliances));
        
        // Check for surges
        currentAlerts.addAll(StatusCalculator.checkForSurges(appliances, settings));
        
        // Calculate total current (only valid readings)
        double totalCurrent = appliances.stream()
                .filter(Appliance::isValid)
                .mapToDouble(Appliance::getCurrentAmps)
                .sum();
        
        // Update socket groups
        for (SocketGroup group : socketGroups.values()) {
            currentAlerts.addAll(StatusCalculator.updateSocketGroupStatus(group));
        }
        
        // Update whole house status
        currentAlerts.addAll(StatusCalculator.updateWholeHouseStatus(totalCurrent, settings));
        
        // Add new alerts
        alerts.addAll(currentAlerts);
        
        // Update energy and cost
        updateEnergyAndCost(totalCurrent);
        
        // Update UI
        updateUI(totalCurrent, currentAlerts);
        
        // Generate recommendations
        updateRecommendations(totalCurrent);
    }
    
    private void updateEnergyAndCost(double totalCurrent) {
        long currentTime = System.nanoTime();
        if (lastUpdateTime > 0) {
            double timeHours = (currentTime - lastUpdateTime) / 3_600_000_000_000.0;
            double power = settings.getVoltage() * totalCurrent; // Watts
            double energyIncrement = (power / 1000.0) * timeHours; // kWh
            totalEnergy += energyIncrement;
        }
        lastUpdateTime = currentTime;
    }
    
    private void updateUI(double totalCurrent, List<models.Alert> newAlerts) {
        // Update summary
        totalCurrentLabel.setText(String.format("Total Current: %.2f A", totalCurrent));
        
        double power = settings.getVoltage() * totalCurrent;
        powerLabel.setText(String.format("Estimated Power: %.2f W", power));
        
        energyLabel.setText(String.format("Session Energy: %.3f kWh", totalEnergy));
        
        double cost = totalEnergy * settings.getTariff();
        costLabel.setText(String.format("Session Cost: GHS %.2f", cost));
        
        // Update whole house status
        String houseStatus = "OK";
        String statusColor = "#2ecc71";
        
        if (totalCurrent > settings.getMainLimit()) {
            houseStatus = "DANGER";
            statusColor = "#e74c3c";
        } else if (totalCurrent >= 0.8 * settings.getMainLimit()) {
            houseStatus = "WARNING";
            statusColor = "#f39c12";
        }
        
        statusLabel.setText("Status: " + houseStatus);
        statusLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: " + statusColor + ";");
        
        // Update table
        applianceTable.refresh();
        
        // Update socket groups
        updateSocketGroupPanel();
        
        // Update alerts list (keep last 20)
        for (models.Alert alert : newAlerts) {
            alertList.getItems().add(0, alert.toString());
        }
        while (alertList.getItems().size() > 20) {
            alertList.getItems().remove(alertList.getItems().size() - 1);
        }
    }
    
    private void updateRecommendations(double totalCurrent) {
        List<String> recommendations = StatusCalculator.generateLoadSheddingRecommendations(
            appliances, totalCurrent, settings
        );
        
        recommendationList.getItems().clear();
        recommendationList.getItems().addAll(recommendations);
    }
    
    private void updateSimulationEngine() {
        boolean wasRunning = isRunning;
        if (wasRunning) {
            stopSimulation();
        }
        
        if (settings.getSimulationMode().equals("Random")) {
            simulationEngine = new RandomSimulation();
        } else {
            simulationEngine = new ScriptedSimulation();
        }
        
        if (wasRunning) {
            startSimulation();
        }
    }
}