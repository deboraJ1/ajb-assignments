import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Sequences;
import presenter.WindowPresenter;
import view.Window;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Window view = new Window();
        Sequences model = new Sequences();
        WindowPresenter presenter = new WindowPresenter(view, model);

        primaryStage.setScene(new Scene(view.getRoot(), 600, 400));
        primaryStage.setTitle("Assignment3");
        primaryStage.show();
    }
}
