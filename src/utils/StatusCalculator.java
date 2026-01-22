package utils;

import models.Appliance;
import models.SocketGroup;
import models.Alert;
import models.Settings;
import java.util.List;
import java.util.ArrayList;

public class StatusCalculator {
    
    public static List<Alert> updateSocketGroupStatus(SocketGroup group) {
        List<Alert> alerts = new ArrayList<>();
        double groupCurrent = group.getTotalCurrent();
        
        if (groupCurrent > 13.0) {
            group.setStatus("DANGER");
            Appliance highest = group.getHighestCurrentAppliance();
            String applianceName = highest != null ? highest.getName() : "unknown";
            alerts.add(new Alert(
                group.getName() + " socket group overloaded (" + 
                String.format("%.1f", groupCurrent) + "A). Highest load: " + applianceName,
                "DANGER"));
        } else if (groupCurrent >= 10.0) {
            group.setStatus("WARNING");
            alerts.add(new Alert(
                group.getName() + " high load (" + 
                String.format("%.1f", groupCurrent) + "A). Avoid adding high power appliances.",
                "WARNING"));
        } else {
            group.setStatus("OK");
        }
        
        return alerts;
    }
    
    public static List<Alert> updateWholeHouseStatus(double totalCurrent, Settings settings) {
        List<Alert> alerts = new ArrayList<>();
        double mainLimit = settings.getMainLimit();
        
        if (totalCurrent > mainLimit) {
            alerts.add(new Alert(
                "Total load exceeded main limit (" + 
                String.format("%.1f", totalCurrent) + "A / " + 
                String.format("%.1f", mainLimit) + "A)",
                "DANGER"));
        } else if (totalCurrent >= 0.8 * mainLimit) {
            alerts.add(new Alert(
                "Approaching main limit (" + 
                String.format("%.1f", totalCurrent) + "A / " + 
                String.format("%.1f", mainLimit) + "A)",
                "WARNING"));
        }
        
        return alerts;
    }
    
    public static List<Alert> checkForSurges(List<Appliance> appliances, Settings settings) {
        List<Alert> alerts = new ArrayList<>();
        
        for (Appliance appliance : appliances) {
            double delta = appliance.getCurrentAmps() - appliance.getPreviousCurrent();
            if (delta >= settings.getSurgeThreshold()) {
                appliance.setStatus("SURGE");
                alerts.add(new Alert(
                    "Surge detected on " + appliance.getName() + ": +" + 
                    String.format("%.1f", delta) + "A",
                    "WARNING"));
            }
        }
        
        return alerts;
    }
    
    public static List<Alert> validateReadings(List<Appliance> appliances) {
        List<Alert> alerts = new ArrayList<>();
        
        for (Appliance appliance : appliances) {
            if (appliance.getCurrentAmps() <= 0) {
                appliance.setStatus("INVALID");
                alerts.add(new Alert(
                    "Sensor fault on " + appliance.getName() + ": Invalid reading (≤0A)",
                    "WARNING"));
            } else if (appliance.getCurrentAmps() > appliance.getMaxCurrent()) {
                appliance.setStatus("INVALID");
                alerts.add(new Alert(
                    "Sensor fault on " + appliance.getName() + ": Reading exceeds maximum (" + 
                    String.format("%.1f", appliance.getCurrentAmps()) + "A > " + 
                    String.format("%.1f", appliance.getMaxCurrent()) + "A)",
                    "WARNING"));
            } else if (!appliance.getStatus().equals("SURGE")) {
                appliance.setStatus("OK");
            }
        }
        
        return alerts;
    }
    
    public static List<String> generateLoadSheddingRecommendations(
            List<Appliance> appliances, double totalCurrent, Settings settings) {
        List<String> recommendations = new ArrayList<>();
        
        if (totalCurrent <= settings.getMainLimit()) {
            return recommendations;
        }
        
        List<Appliance> nonEssential = new ArrayList<>();
        for (Appliance app : appliances) {
            if (app.getPriority().equals("Non-essential") && app.isValid()) {
                nonEssential.add(app);
            }
        }
        
        nonEssential.sort((a1, a2) -> Double.compare(a2.getCurrentAmps(), a1.getCurrentAmps()));
        
        double excessCurrent = totalCurrent - settings.getMainLimit();
        double saved = 0;
        
        recommendations.add("Reduce load by " + String.format("%.1f", excessCurrent) + "A:");
        
        for (Appliance app : nonEssential) {
            if (saved >= excessCurrent) break;
            recommendations.add("• Turn off " + app.getName() + " (" + 
                              String.format("%.1f", app.getCurrentAmps()) + "A)");
            saved += app.getCurrentAmps();
        }
        
        if (saved < excessCurrent) {
            recommendations.add("Warning: May need to turn off essential appliances");
        }
        
        return recommendations;
    }
}
