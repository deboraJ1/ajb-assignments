import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.EthanolMolecule;
import model.Molecule;
import model.WaterMolecule;

/**
 * the main program
 * Daniel Huson, 4.2021
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        Window view = new Window(); // create view
        Molecule model = new EthanolMolecule(); // create model

        // set stage
        stage.setScene(new Scene(view.getRoot()));
        stage.setTitle("MoleculeView");

        WindowPresenter presenter = new WindowPresenter(stage, view, model);

        stage.show();
    }
}
