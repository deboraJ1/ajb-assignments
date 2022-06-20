import controller.Controller;
import view.KmerView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {


    @Override
    public void start(Stage primaryStage) throws Exception{
        Controller controller = new Controller();
        KmerView kmerView = controller.getView();

        primaryStage.setTitle("Code4MersFX");
        primaryStage.setScene(new Scene(kmerView.getRoot(), 500, 500));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
