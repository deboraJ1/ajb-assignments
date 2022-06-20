package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

/**
 * Abstract class AbstractWindow provides basic functionality needed for a Window, like
 * displaying error messages or access to the view.WindowController used to add eventHandler to more concrete control elements of extending classes.
 */
public abstract class AbstractWindow {

    // view.WindowController holds all elements of the GUI which shall be accessed.
    private final WindowController controller;

    /**
     * The class Window contains simple functionality of the view which do not require interaction with presenter and model.
     */
    public AbstractWindow(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Window.fxml"));
        try{
            Parent root = loader.load();
        }
        catch (Exception f){
            System.err.println("Window Controller was not initialized correctly: " + f.getMessage());
        }
        this.controller = loader.getController();
    }


    // Exception handling
    /**
     * Display an error message in the GUI.
     * @param message - the message to be displayed.
     */
    public void displayInfoMessage(String title, String message){
        Alert infoAllert = new Alert(Alert.AlertType.INFORMATION);
        if(title != null && !title.isBlank()){
            infoAllert.setHeaderText(title);
        }
        else{
            infoAllert.setHeaderText("Information");
        }
        if(message != null && !message.isBlank()) {
            infoAllert.setContentText(message);
        }else{
            infoAllert.setContentText("Tryed to show an information but the content was null or empty.");
        }
        infoAllert.showAndWait();
    }

    /**
     * Display an error message in the GUI with specified header text of the window.
     * @param headerText - header text of the Window displaying the error message
     * @param message - the error message to be displayed.
     */
    public void displayErrorMessage(String headerText, String message){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        if(headerText != null && !headerText.isBlank()){
            errorAlert.setHeaderText(headerText);
        }
        else{
            errorAlert.setHeaderText("Information");
        }
        if(message != null && !message.isBlank()) {
            errorAlert.setContentText(message);
        }
        else{
            errorAlert.setContentText("Tryed to show an information but the content was null or empty.");
        }
        errorAlert.showAndWait();
    }

    //getter
    public WindowController getController() {
        return controller;
    }
}

