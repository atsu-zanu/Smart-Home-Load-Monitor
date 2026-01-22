import javafx.application.Application;
import javafx.stage.Stage;
import controllers.MainController;

public class Main extends Application {
    
    @Override
    public void start(Stage primaryStage) {
        MainController mainController = new MainController();
        mainController.start(primaryStage);
    }
    
    public static void main(String[] args) {
        launch(args);
    }
}