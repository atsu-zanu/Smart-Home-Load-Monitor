package models;

import java.util.ArrayList;
import java.util.List;

public class SocketGroup {
    private String name;
    private List<Appliance> appliances;
    private String status; // "OK", "WARNING", "DANGER"
    
    public SocketGroup(String name) {
        this.name = name;
        this.appliances = new ArrayList<>();
        this.status = "OK";
    }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public List<Appliance> getAppliances() { return appliances; }
    
    public void addAppliance(Appliance appliance) {
        appliances.add(appliance);
    }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public double getTotalCurrent() {
        return appliances.stream()
                .filter(Appliance::isValid)
                .mapToDouble(Appliance::getCurrentAmps)
                .sum();
    }
    
    public Appliance getHighestCurrentAppliance() {
        return appliances.stream()
                .filter(Appliance::isValid)
                .max((a1, a2) -> Double.compare(a1.getCurrentAmps(), a2.getCurrentAmps()))
                .orElse(null);
    }
}
