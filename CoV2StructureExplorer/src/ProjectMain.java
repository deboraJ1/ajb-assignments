import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import model.pdbaccess.FileParser;
import model.pdbaccess.PDBWebClient;
import view.Window;

import java.io.IOException;

public class ProjectMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Window view = new Window();
        PDBWebClient webClient = null;
        FileParser fileParser = new FileParser();
        try{
            webClient = new PDBWebClient();
            WindowPresenter presenter = new WindowPresenter(view, webClient, fileParser);
        }
        catch (IOException e){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error Accessing PDB Server");
            errorAlert.setContentText("Sorry, we can not reach the PDB Server: " + e.getMessage() + "\n Please check your Internet connection. ");
            errorAlert.showAndWait();
        }

        primaryStage.setScene(new Scene(view.getRoot(), 900, 700));
        primaryStage.setTitle("SARSCoV-2 Structure Explorer");
        primaryStage.show();
    }
}