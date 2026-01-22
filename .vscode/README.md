# Smart Home Load Monitor

A JavaFX desktop application for monitoring electrical loads in Ghanaian homes using 13A socket outlets.

## Project Structure
```
HelloJavaFX/
├── .vscode/              # VS Code configuration
├── src/                  # Source code
│   ├── Main.java        # Application entry point
│   ├── models/          # Data models
│   ├── controllers/     # UI controllers
│   ├── simulation/      # Simulation engines
│   └── utils/           # Utility classes
└── README.md
```

## Setup Instructions

1. Ensure Java JDK 11+ is installed
2. Ensure JavaFX SDK is at: `C:\javafx-sdk-21.0.10\javafx-sdk-21.0.10`
3. Open this folder in VS Code
4. Install "Extension Pack for Java" extension
5. Press F5 to run

## Features

- Real-time appliance current monitoring
- Socket group overload detection
- Whole house load tracking
- Surge detection
- Invalid sensor reading detection
- Load shedding recommendations
- Energy and cost tracking
- Two simulation modes: Random and Scripted

## Usage

1. Click **Start** to begin simulation
2. Click **Stop** to pause
3. Click **Settings** to configure:
   - Voltage (default: 230V)
   - Main limit (default: 40A)
   - Surge threshold (default: 3A)
   - Tariff rate (GHS/kWh)
   - Simulation mode

## Testing Scenarios

### Random Mode
- Continuously generates random but realistic appliance readings
- Occasional surges and invalid readings

### Scripted Mode
- Plays through predefined scenarios:
  - Steps 1-5: Normal operation
  - Steps 6-10: Kitchen socket overload
  - Steps 11-15: Warning level
  - Steps 16-20: Whole house overload
  - Steps 21-25: Surge event
  - Steps 26-30: Invalid sensor readings

## Status Indicators

- **Green (OK)**: Normal operation
- **Orange (WARNING)**: Approaching limits
- **Red (DANGER)**: Overload condition
- **Gray (INVALID)**: Sensor fault
- **Purple (SURGE)**: Surge detected

## Author
Zanu Atsu Stephen /Group 6 members.
Built with JavaFX for ECE Mini Project