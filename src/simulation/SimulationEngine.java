package simulation;

import models.Appliance;
import java.util.List;

public interface SimulationEngine {
    void updateReadings(List<Appliance> appliances);
    void reset();
}
