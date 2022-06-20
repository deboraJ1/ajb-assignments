package view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;

/**
 * This is an active Window, which extends AbstractWindow.
 * Active means, that functionality concerning only the view is handled directly in the Window class, like displaying About text
 */
public class Window extends AbstractWindow {

    private final BorderPane root;
    private static final String about = "This little program can be used to read files, written in Newick format, " +
            "which are then displayed as tree in the tree tab. \n" +
            "The plain text version of the tree in newick format is displayed in the text tab. \n" +
            "The output of the read file can be cleared using the clear button in the text tab or in the menu under Edit > Clear \n \n" +
            "This program is part of the Advanced Java for Bioinformatics course at University Tübingen in Germany and was created by Debora Jutz";

    public Window(){
        super(); //contains WindowController
        this.root = super.getController().getBorderPane();

        addEventHandler();
        addBindings();
    }

    /**
     * Adds all EventHandler to Control elements where no interaction with WindowPresenter is needed.
     */
    private void addEventHandler() {
        // clear menu item and clear Button with same functionality
        getController().getClearMenuItem().setOnAction(a -> clearTxtArea());
        getController().getClearButtonBTxtTab().setOnAction(a -> clearTxtArea());

        getController().getAboutMenuItem().setOnAction(a -> createAboutStage().show());
        getController().getCloseFileMenuItem().setOnAction(a -> System.exit(0));

        // update character status when text in textArea changes
        getController().getOutputAreaCTxtTab().textProperty().addListener((c, o, n) -> getController().getCharLabelBTxtTab().textProperty().setValue(n.chars().count() + " chars"));
    }

    /**
     * Add Bindings to control elements, which are depending on each other.
     */
    private void addBindings(){
        // disable clear options when Output Area is empty
        getController().getClearButtonBTxtTab().disableProperty().bind(getController().getOutputAreaCTxtTab().textProperty().isEmpty());
        getController().getClearMenuItem().disableProperty().bind(getController().getOutputAreaCTxtTab().textProperty().isEmpty());

    }


    /**
     * create a new Stage to display the about text.
     * @return Stage to be displayed.
     */
    public Stage createAboutStage(){
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About");
        TextArea aboutText = new TextArea(about);
        aboutText.setWrapText(true);
        aboutStage.setScene(new Scene(aboutText, 400, 250));
        return aboutStage;
    }

    /**
     * Clear the outputArea of the text tab
     */
    private void clearTxtArea() {
        getController().getOutputAreaCTxtTab().setText("");
    }

    public Parent getRoot() {
        return this.root;
    }

}
