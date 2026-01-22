package simulation;

import models.Appliance;
import java.util.List;

public class ScriptedSimulation implements SimulationEngine {
    private int step;
    
    public ScriptedSimulation() {
        this.step = 0;
    }
    
    @Override
    public void updateReadings(List<Appliance> appliances) {
        step++;
        
        // Scenario 1: Normal operation (steps 1-5)
        if (step <= 5) {
            setNormalReadings(appliances);
        }
        // Scenario 2: Socket group overload (steps 6-10)
        else if (step <= 10) {
            setSocketOverload(appliances);
        }
        // Scenario 3: Warning before overload (steps 11-15)
        else if (step <= 15) {
            setWarningLevel(appliances);
        }
        // Scenario 4: Whole house limit exceeded (steps 16-20)
        else if (step <= 20) {
            setWholeHouseOverload(appliances);
        }
        // Scenario 5: Surge event (steps 21-25)
        else if (step <= 25) {
            setSurgeEvent(appliances);
        }
        // Scenario 6: Invalid sensor readings (steps 26-30)
        else if (step <= 30) {
            setInvalidReadings(appliances);
        }
        // Loop back
        else {
            step = 1;
            setNormalReadings(appliances);
        }
    }
    
    private void setNormalReadings(List<Appliance> appliances) {
        for (Appliance app : appliances) {
            switch (app.getName()) {
                case "Refrigerator": app.setCurrentAmps(2.5); break;
                case "Air Conditioner": app.setCurrentAmps(7.5); break;
                case "Microwave": app.setCurrentAmps(0.5); break;
                case "Electric Kettle": app.setCurrentAmps(0.0); break;
                case "TV": app.setCurrentAmps(1.5); break;
                case "Washing Machine": app.setCurrentAmps(0.0); break;
                case "Iron": app.setCurrentAmps(0.0); break;
                case "Fan": app.setCurrentAmps(0.8); break;
                case "Laptop": app.setCurrentAmps(0.5); break;
                case "Water Heater": app.setCurrentAmps(0.0); break;
            }
        }
    }
    
    private void setSocketOverload(List<Appliance> appliances) {
        for (Appliance app : appliances) {
            if (app.getSocketGroup().equals("Kitchen")) {
                switch (app.getName()) {
                    case "Refrigerator": app.setCurrentAmps(2.5); break;
                    case "Microwave": app.setCurrentAmps(5.5); break;
                    case "Electric Kettle": app.setCurrentAmps(10.5); break;
                    default: app.setCurrentAmps(1.0); break;
                }
            }
        }
    }
    
    private void setWarningLevel(List<Appliance> appliances) {
        for (Appliance app : appliances) {
            if (app.getSocketGroup().equals("Living Room")) {
                switch (app.getName()) {
                    case "TV": app.setCurrentAmps(1.5); break;
                    case "Air Conditioner": app.setCurrentAmps(9.0); break;
                    default: app.setCurrentAmps(0.5); break;
                }
            }
        }
    }
    
    private void setWholeHouseOverload(List<Appliance> appliances) {
        for (Appliance app : appliances) {
            switch (app.getName()) {
                case "Refrigerator": app.setCurrentAmps(2.5); break;
                case "Air Conditioner": app.setCurrentAmps(9.0); break;
                case "Microwave": app.setCurrentAmps(5.5); break;
                case "Electric Kettle": app.setCurrentAmps(10.5); break;
                case "TV": app.setCurrentAmps(1.5); break;
                case "Washing Machine": app.setCurrentAmps(7.0); break;
                case "Iron": app.setCurrentAmps(9.5); break;
                case "Fan": app.setCurrentAmps(0.8); break;
                case "Laptop": app.setCurrentAmps(0.5); break;
                case "Water Heater": app.setCurrentAmps(11.5); break;
            }
        }
    }
    
    private void setSurgeEvent(List<Appliance> appliances) {
        setNormalReadings(appliances);
        for (Appliance app : appliances) {
            if (app.getName().equals("Air Conditioner")) {
                app.setCurrentAmps(12.0); // Surge from 7.5A to 12A
            }
        }
    }
    
    private void setInvalidReadings(List<Appliance> appliances) {
        setNormalReadings(appliances);
        for (Appliance app : appliances) {
            if (app.getName().equals("Microwave")) {
                app.setCurrentAmps(-2.0); // Invalid negative reading
            }
            if (app.getName().equals("Iron")) {
                app.setCurrentAmps(20.0); // Exceeds max
            }
        }
    }
    
    @Override
    public void reset() {
        step = 0;
    }
}
