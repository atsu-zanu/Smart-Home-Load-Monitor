package simulation;

import models.Appliance;
import java.util.List;
import java.util.Random;

public class RandomSimulation implements SimulationEngine {
    private Random random;
    private int updateCount;
    
    public RandomSimulation() {
        this.random = new Random();
        this.updateCount = 0;
    }
    
    @Override
    public void updateReadings(List<Appliance> appliances) {
        updateCount++;
        
        for (Appliance appliance : appliances) {
            double baseCurrent = getBaseCurrentForAppliance(appliance.getName());
            double variation = baseCurrent * 0.2; // ±20% variation
            double newCurrent = baseCurrent + (random.nextDouble() * variation * 2 - variation);
            
            // Occasional surge (5% chance)
            if (random.nextDouble() < 0.05) {
                newCurrent += 3.0 + random.nextDouble() * 2.0;
            }
            
            // Occasional invalid reading (3% chance)
            if (random.nextDouble() < 0.03) {
                newCurrent = random.nextBoolean() ? -1.0 : appliance.getMaxCurrent() + 5.0;
            }
            
            appliance.setCurrentAmps(Math.max(0, newCurrent));
        }
    }
    
    private double getBaseCurrentForAppliance(String name) {
        switch (name) {
            case "Refrigerator": return 2.5;
            case "Air Conditioner": return 8.0;
            case "Microwave": return 5.0;
            case "Electric Kettle": return 10.0;
            case "TV": return 1.5;
            case "Washing Machine": return 6.0;
            case "Iron": return 9.0;
            case "Fan": return 0.8;
            case "Laptop": return 0.5;
            case "Water Heater": return 11.0;
            default: return 2.0;
        }
    }
    
    @Override
    public void reset() {
        updateCount = 0;
    }
}
