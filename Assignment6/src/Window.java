import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.Objects;

/**
 * this creates scene graph from a fxml file
 * Daniel Huson, 4.2021
 */
public class Window {
    private final WindowController controller;
    private final Parent root;

    public Window() throws IOException {
        try (var ins = Objects.requireNonNull(getClass().getResource("Window.fxml")).openStream()) {
            var fxmlLoader = new FXMLLoader();
            root = fxmlLoader.load(ins);
            controller = fxmlLoader.getController();
        }
    }


    /**
     * Display an error message in the GUI with specified header text of the window.
     * @param headerText - header text of the Window displaying the error message
     * @param message - the error message to be displayed.
     */
    public void displayErrorMessage(String headerText, String message){
        if(headerText != null && message != null) {
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText(headerText);
            errorAlert.setContentText(message);
            errorAlert.showAndWait();
        }
    }

    /**
     * Display an error message in the GUI with specified header text of the window.
     * @param headerText - header text of the Window displaying the error message
     * @param message - the error message to be displayed.
     */
    public void displayWarning(String headerText, String message){
        if(headerText != null && message != null) {
            Alert warning = new Alert(Alert.AlertType.WARNING);
            warning.setHeaderText(headerText);
            warning.setContentText(message);
            warning.show();
        }
    }

    public WindowController getController() {
        return controller;
    }

    public Parent getRoot() {
        return root;
    }
}
