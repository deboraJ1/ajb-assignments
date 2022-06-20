import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import view.Window;

public class TreeToolMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Window view = new Window();
        WindowPresenter presenter = new WindowPresenter(view);

        primaryStage.setScene(new Scene(view.getRoot()));
        primaryStage.setTitle("Assignment4");
        primaryStage.show();
    }
}
