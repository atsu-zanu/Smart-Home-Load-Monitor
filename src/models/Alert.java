package models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Alert {
    private String message;
    private String timestamp;
    private String type; // "INFO", "WARNING", "DANGER"
    
    public Alert(String message, String type) {
        this.message = message;
        this.type = type;
        this.timestamp = LocalDateTime.now().format(
            DateTimeFormatter.ofPattern("HH:mm:ss"));
    }
    
    public String getMessage() { return message; }
    public String getTimestamp() { return timestamp; }
    public String getType() { return type; }
    
    @Override
    public String toString() {
        return "[" + timestamp + "] " + type + ": " + message;
    }
}
