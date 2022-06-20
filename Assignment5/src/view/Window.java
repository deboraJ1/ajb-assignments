package view;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

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

    //don't just change order of this lists values --> are referenced in switch case in method drawTree()
    private static final List<String> lineStyles = new ArrayList<>(){{ add("Straight"); add("Angular"); add("Quad"); add("Cubic");}};

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
        getController().getAboutMenuItem().setOnAction(a -> createAboutStage().show());
        getController().getCloseFileMenuItem().setOnAction(a -> System.exit(0));

        // update character status when text in textArea changes
        getController().getOutputAreaCTxtTab().textProperty().addListener((c, oldValue, newValue) -> getController().getCharLabelBTxtTab().textProperty().setValue(newValue.chars().count() + " chars"));
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

    public Parent getRoot() {
        return this.root;
    }

}
