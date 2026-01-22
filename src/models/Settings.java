package models;

public class Settings {
    private double voltage;
    private double mainLimit;
    private double surgeThreshold;
    private double tariff;
    private String simulationMode; // "Random" or "Scripted"
    
    public Settings() {
        this.voltage = 230.0;
        this.mainLimit = 40.0;
        this.surgeThreshold = 3.0;
        this.tariff = 1.5; // GHS per kWh (example)
        this.simulationMode = "Random";
    }
    
    // Getters and Setters
    public double getVoltage() { return voltage; }
    public void setVoltage(double voltage) { this.voltage = voltage; }
    
    public double getMainLimit() { return mainLimit; }
    public void setMainLimit(double mainLimit) { this.mainLimit = mainLimit; }
    
    public double getSurgeThreshold() { return surgeThreshold; }
    public void setSurgeThreshold(double surgeThreshold) { this.surgeThreshold = surgeThreshold; }
    
    public double getTariff() { return tariff; }
    public void setTariff(double tariff) { this.tariff = tariff; }
    
    public String getSimulationMode() { return simulationMode; }
    public void setSimulationMode(String simulationMode) { this.simulationMode = simulationMode; }
}
