package view;

import controller.Controller;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.stage.Stage;


/**
 * View class is one part to realize the Model-View-Controller.Controller principle.
 * The view contains the elements which are displayed in the scene. Those can be updated from the @see Model.Model.
 */
public class KmerView {

    private Controller controller;
    private BorderPane root;

    // Text elements for input and output
    private TextField dnaInputField;
    private final boolean isFocusOfStart = true; // helping construct for dnaInputField
    private TextArea outputArea;

    // CheckBoxes for selection of what is displayed
    private CheckBox showCodeCheckBox, showKmersCheckBox, showCountsCheckBox;
    // Buttons to clear output area, analyze input sequence (which is displayed in output then) and quit program
    private Button clearButton, analyzeButton, quitButton;
    private Slider fontSlider;

    /**
     * Constructor of this class to create a new instance of KmerView
     * @param controller - the controller to manage communication between this class and the model.
     *                   If null, an error message will be displayed.
     */
    public KmerView(Controller controller){
        if(controller != null){
            this.controller = controller;
            this.root = new BorderPane();

            //create Control elements with action handling and add those to root element
            createTextElements();
            this.root.setCenter(outputArea);
            this.root.setTop(dnaInputField);

            createCheckBoxes();
            fillVBox();

            createButtons();
            createFontSlider();
            fillHBox();
        }
        else {
            System.err.println("BorderPane can not be null!");
        }

    }

    /**
     * Create the text elements for this scene.
     * A field to accept a DNA sequence as input and
     * An area where the 4mers, which could have been extracted from the input, are displayed
     * as well as their hash codes and counts (according to the selected Checkboxes)
     */
    private void createTextElements(){

        this.dnaInputField = new TextField();
        this.dnaInputField.setPromptText("Enter your DNA sequence here.");
        this.dnaInputField.setVisible(true);

        this.outputArea = new TextArea();
        this.outputArea.setEditable(false);
        this.outputArea.setVisible(true);
    }

    /**
     * create Checkboxes to be shown on the right of the scene:
     * Depending on the selection 4mers, their hash codes and/or their counts are displayed or not.
     */
    private void createCheckBoxes(){
        this.showCodeCheckBox = new CheckBox();
        this.showCodeCheckBox.setSelected(true);
        this.showCodeCheckBox.setText("Show Hash Code");
        addCheckBoxListener(this.showCodeCheckBox);
        this.showCodeCheckBox.setAllowIndeterminate(false);
        this.showCodeCheckBox.setVisible(true);

        this.showKmersCheckBox = new CheckBox();
        this.showKmersCheckBox.setSelected(true);
        this.showKmersCheckBox.setText("Show 4mer");
        addCheckBoxListener(this.showKmersCheckBox);
        this.showKmersCheckBox.setAllowIndeterminate(false);
        this.showKmersCheckBox.setVisible(true);

        this.showCountsCheckBox = new CheckBox();
        this.showCountsCheckBox.setSelected(true);
        this.showCountsCheckBox.setText("Show Count");
        addCheckBoxListener(this.showCountsCheckBox);
        this.showCountsCheckBox.setAllowIndeterminate(false);
        this.showCountsCheckBox.setVisible(true);

    }

    /**
     * Adds an EventHandler to listen for changes in the CheckBox selection.
     * @param checkBox - checkBox, that shall have a Listener.
     */
    private void addCheckBoxListener(CheckBox checkBox){
        if(checkBox != null) {
            checkBox.setOnAction(e -> {updateOutputArea();});
        }
    }

    /**
     * Creates the VBox to be displayed in the right part of this scene and adds the checkBoxes.
     */
    private void fillVBox(){
        VBox checkBoxesBoxRight = new VBox();
        checkBoxesBoxRight.getChildren().addAll(showCodeCheckBox, showKmersCheckBox, showCountsCheckBox);
        checkBoxesBoxRight.setAlignment(Pos.TOP_LEFT);  //on right side of scene, the box is aligned on top left
        checkBoxesBoxRight.setVisible(true);

        this.root.setRight(checkBoxesBoxRight);
    }

    //_____________________________________________________________________________________________Bottom Part of scene
    /**
     * Create Buttons to be shown at the bottom of the scene to quit the program, analyze the given DNA sequence or clear the output area.
     */
    private void createButtons(){
        // Clear button clears input and output when fired.
        this.clearButton = new Button("Clear");
        this.clearButton.setOnAction(a -> {
                dnaInputField.setText("Enter your DNA sequence here.");
                outputArea.setText("");
        });
        addMouseShadow(this.clearButton);
        this.clearButton.setVisible(true);

        // Analyze executes analysis of the DNA sequence when fired.
        this.analyzeButton = new Button("Analyze");
        this.analyzeButton.setOnAction(e -> {updateOutputArea();});
        addMouseShadow(this.analyzeButton);
        this.analyzeButton.setDefaultButton(true);
        this.analyzeButton.setVisible(true);

        this.quitButton = new Button("Quit");
        this.quitButton.setOnAction(e -> {
                System.exit(0);
        });
        addMouseShadow(this.quitButton);
        this.quitButton.setVisible(true);
    }

    /**
     * Creates a font slider to change the font size of input field and output area according to the value selected by the slider.
     */
    private void createFontSlider(){
        //fontSlider with min Value 7 (below is probably too small), max Value 50 and current Value 12.
        this.fontSlider = new Slider(5, 55, 12);

        this.fontSlider.setShowTickLabels(true);    //show numbers on slider
        this.fontSlider.setShowTickMarks(true);     //show little lines between the labels
        this.fontSlider.setMajorTickUnit(10);       //step size between two numbers/labels
        this.fontSlider.setMinorTickCount(5);       //step size between two marks/lines
        this.fontSlider.valueProperty().addListener((observableValue, oldValue, newValue) -> {
                dnaInputField.setFont(new Font(dnaInputField.getFont().getName(), fontSlider.getValue()));
                outputArea.setFont(new Font(outputArea.getFont().getName(), fontSlider.getValue()));
        });
    }

    /**
     * Creates the HBox to be displayed in the bottom of this scene and adds the buttons and the fontSlider.
     */
    private void fillHBox(){
        HBox buttonBoxBottom = new HBox();
        buttonBoxBottom.getChildren().addAll(clearButton, analyzeButton, quitButton, fontSlider);
        buttonBoxBottom.setAlignment(Pos.BOTTOM_CENTER);
        buttonBoxBottom.setVisible(true);

        this.root.setBottom(buttonBoxBottom);
    }

    /**
     * Adds an EventHandler to show a shadow effect, while the mouse is over the Button button.
     * @param button - button, that shall have shadow functionality.
     */
    private void addMouseShadow(Button button){
        if(button != null) {
            button.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> button.setEffect(new DropShadow()));
            //Removing the shadow when the mouse cursor is off
            button.addEventHandler(MouseEvent.MOUSE_EXITED, e -> button.setEffect(null));
        }
    }

    //__________________________________________________________________________________________general parts

    /**
     * Updates the Output Area according to the current selection.
     * Different combinations of selection of the Checkboxes determine which of the following to show: code, kmers and counts.
     * This method requests the controller for the 4mers of the actual DNA sequence in the input field and
     * displays the resulting answer according to the mentioned selection in the output area.
     */
    private void updateOutputArea(){
        String input = dnaInputField.getText();
        String result = "Please first enter a DNA sequence to be analyzed.";    //show this text if no DNA was entered.
        if (input != null && !input.isBlank()) {
            result = controller.getAnalysisResult(input, showCodeCheckBox.isSelected(), showKmersCheckBox.isSelected(), showCountsCheckBox.isSelected());
        }
        //might be an error text if an error occurred.
        outputArea.setText(result);
    }

    public BorderPane getRoot(){
        return this.root;
    }
}
