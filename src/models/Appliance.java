package models;

public class Appliance {
    private String name;
    private String location;
    private String socketGroup;
    private double currentAmps;
    private double previousCurrent;
    private String priority; // "Essential" or "Non-essential"
    private String status; // "OK", "WARNING", "DANGER", "INVALID", "SURGE"
    private double maxCurrent;
    private boolean isOn;
    
    public Appliance(String name, String location, String socketGroup, 
                     String priority, double maxCurrent) {
        this.name = name;
        this.location = location;
        this.socketGroup = socketGroup;
        this.priority = priority;
        this.maxCurrent = maxCurrent;
        this.currentAmps = 0.0;
        this.previousCurrent = 0.0;
        this.status = "OK";
        this.isOn = true;
    }
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getSocketGroup() { return socketGroup; }
    public void setSocketGroup(String socketGroup) { this.socketGroup = socketGroup; }
    
    public double getCurrentAmps() { return currentAmps; }
    public void setCurrentAmps(double currentAmps) { 
        this.previousCurrent = this.currentAmps;
        this.currentAmps = currentAmps; 
    }
    
    public double getPreviousCurrent() { return previousCurrent; }
    
    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getMaxCurrent() { return maxCurrent; }
    public void setMaxCurrent(double maxCurrent) { this.maxCurrent = maxCurrent; }
    
    public boolean isOn() { return isOn; }
    public void setOn(boolean isOn) { this.isOn = isOn; }
    
    public boolean isValid() {
        return currentAmps > 0 && currentAmps <= maxCurrent;
    }
}
