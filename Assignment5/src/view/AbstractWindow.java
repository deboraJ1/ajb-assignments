package view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;

import java.io.IOException;

/**
 * Abstract class AbstractWindow provides basic functionality needed for a Window, like
 * displaying error messages or access to the WindowController used to add eventHandler to more concrete control elements of extending classes.
 */
public abstract class AbstractWindow {

        // WindowController holds all elements of the GUI which shall be accessed.
        private final WindowController controller;

        /**
         * The class Window contains simple functionality of the view which do not require interaction with presenter and model.
         */
        public AbstractWindow(){
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Window.fxml"));
            try{
                Parent root = loader.load();
            }
            catch (IOException e){
                System.err.println("Error loading Parent root in Abstract Window: " +  e.getMessage());
            }
            this.controller = loader.getController();
        }


        // Exception handling
        /**
         * Display an error message in the GUI.
         * @param message - the message to be displayed.
         */
        public void displayErrorMessage(String message){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText("Error");
            errorAlert.setContentText(message);
            errorAlert.showAndWait();
        }

        /**
         * Display an error message in the GUI with specified header text of the window.
         * @param headerText - header text of the Window displaying the error message
         * @param message - the error message to be displayed.
         */
        public void displayErrorMessage(String headerText, String message){
            Alert errorAlert = new Alert(Alert.AlertType.ERROR);
            errorAlert.setHeaderText(headerText);
            errorAlert.setContentText(message);
            errorAlert.showAndWait();
        }

        //getter
        public WindowController getController() {
            return controller;
        }
    }

