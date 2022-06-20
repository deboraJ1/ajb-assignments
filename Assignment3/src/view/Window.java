package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.awt.*;
import java.io.IOException;

/**
 * Class window provides methods to add EventHandler to the different control elements in the GUI specified by Window.fxml.
 * Window uses WindowController to add given eventHandler to those control elements.
 */
public class Window {

    // WindowController holds all elements of the GUI which shall be accessed.
    private final WindowController controller;

    /**
     * The class Window contains simple functionality of the view which do not require interaction with presenter and model.
     */
    public Window(){
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Window.fxml"));
        try{
            Parent root = loader.load();
        }
        catch (IOException e){
        }
        this.controller = loader.getController();
    }

    //Event Handler for Button
    /**
     * Sets an EventHandler to the Button which causes to apply the specified kmer size in the output area of the distances tab.
     * @param e the event handler to be set to the button apply.
     *        If e is null, no Event Handler will be set.
     */
    public void setApplyButtonEventHandler(EventHandler e){
        if (e != null){
            controller.getApplyButton().setOnAction(e);
        }
    }

    //Event Handler for Checkboxes
    /**
     * Sets an EventHandler to the Checkbox defining whether or not to display headers in the sequence tab.
     * @param e the event handler to be set to the checkbox headers.
     *        If e is null, no Event Handler will be set.
     */
    public void setHeadersCheckBoxEventHandler(EventHandler e){
        if (e != null){
            controller.getHeadersCheckBox().setOnAction(e);
        }
    }

    /**
     * Sets an EventHandler to the Checkbox defining whether or not to display sequences in the sequence tab.
     * @param e the event handler to be set to the checkbox sequences.
     *        If e is null, no Event Handler will be set.
     */
    public void setSequencesCheckBoxEventHandler(EventHandler e){
        if (e != null){
            controller.getSequencesCheckBox().setOnAction(e);
        }
    }

    //Event Handler for Menu Items
    /**
     * Sets an EventHandler to the Menu Item, which opens a file.
     * @param e the event handler to be set to the menu item 'open file'.
     *          If e is null, no Event Handler will be set.
     */
    public void setFileOpenMenuItemEventHandler(EventHandler<ActionEvent> e){
        if(e != null){
            controller.getOpenFileMenuItem().setOnAction(e);
        }
    }

    /**
     * Sets an EventHandler to the Menu Item, which closes the application.
     * @param e the event handler to be set to the menu item 'close'.
     *          If e is null, no Event Handler will be set.
     */
    public void setCloseMenuItemEventHandler(EventHandler<ActionEvent> e){
        if(e != null){
            controller.getCloseFileMenuItem().setOnAction(e);
        }
    }

    /**
     * Sets an EventHandler to the Menu Item, which clears all sequences.
     * @param e the event handler to be set to the menu item 'clear'.
     *          If e is null, no Event Handler will be set.
     */
    public void setClearAllMenuItemEventHandler(EventHandler<ActionEvent> e){
        if(e != null){
            controller.getClearMenuItem().setOnAction(e);
        }
    }

    /**
     * Sets an EventHandler to the Menu Item, which wraps text in the sequence text viewer.
     * @param e the event handler to be set to the menu item 'wrap'.
     *          If e is null, no Event Handler will be set.
     */
    public void setWrapMenuItemEventHandler(EventHandler<ActionEvent> e){
        if(e != null){
            controller.getWrapMenuItem().setOnAction(e);
        }
    }

    /**
     * Sets an EventHandler to the Menu Item, which displays an about text.
     * @param e the event handler to be set to the menu item 'about'.
     *          If e is null, no Event Handler will be set.
     */
    public void setAboutMenuItemEventHandler(EventHandler<ActionEvent> e){
        if(e != null){
            controller.getAboutMenuItem().setOnAction(e);
        }
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
    public BorderPane getRoot(){
        return controller.getBoderPane();
    }

    public WindowController getController() {
        return controller;
    }
}
