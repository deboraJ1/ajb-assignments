package presenter;

import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.KMerAnalysis;
import model.Sequences;
import view.Window;
import view.WindowController;

import java.io.File;
import java.io.IOException;
import java.util.List;

//manages communication between view and model
//reacts to user events and calls model methods to update model
public class WindowPresenter {

    private final static String about = "This little program can be used to read fastA file containing at least one header and sequence. " +
            "Sequences are displayed in the sequence tab with the according headers. " +
            "The distance tab allows to specify a kmer size. For all DNA sequences given the distances of all kmers are then computed and displayed in a grid. \n" +
            "This program is part of the Advanced Java for Bioinformatics course at University Tübingen in Germany and was created by Debora Jutz";

    private Window view;
    private Sequences sequences;

    public WindowPresenter(Window view, Sequences sequences){
        if(view != null){
            this.view = view;
            addEventHandler();
        }
        else{
            System.err.println("Window can not be null!");
        }
        if(sequences != null){
            this.sequences = sequences;
        }
        else {
            this.sequences = new Sequences();
        }
    }

    private void addEventHandler() {
        // Menu Items
        this.view.setFileOpenMenuItemEventHandler(e -> {
            File fastAFile = chooseFastAFile();
            this.sequences = new Sequences();
            try {
                this.sequences.read(fastAFile);
                this.sequences.extractDNAs();
                updateSequenceOutputArea();
            } catch (IOException ioException) {
                this.view.displayErrorMessage("Reading file failed", ioException.getMessage());
            }
        });
        this.view.setCloseMenuItemEventHandler(e -> System.exit(0));
        this.view.setClearAllMenuItemEventHandler(e -> {
            WindowController controller = this.view.getController();
            controller.getTextOutputArea().setText("");
            this.sequences.clear();
            updateStatusLine();
        });
        this.view.setWrapMenuItemEventHandler(e -> {
            //allow to unwrap and wrap text according to current state of output text area
            if(this.view.getController().getTextOutputArea().isWrapText()){
                this.view.getController().getTextOutputArea().setWrapText(false);
                this.view.getController().getWrapMenuItem().setText("Unwrap Text");
            }
            else{
                this.view.getController().getTextOutputArea().setWrapText(true);
                this.view.getController().getWrapMenuItem().setText("Wrap Text");
            }
        });
        this.view.setAboutMenuItemEventHandler(e -> {
            createAboutStage().show();
        });

        // Checkboxes
        this.view.setHeadersCheckBoxEventHandler(e -> updateSequenceOutputArea());
        this.view.setSequencesCheckBoxEventHandler(e -> updateSequenceOutputArea());

        // Button
        this.view.setApplyButtonEventHandler(e -> fillDistanceGridPane());
    }

    private void fillDistanceGridPane() {
        String sizeTxt = this.view.getController().getKmerSizeTextField().getText();
        int kmerSize = Integer.valueOf(sizeTxt);
        List<String> headers = this.sequences.getHeader();
        double [][] distanceMatrix = KMerAnalysis.computeDistances(this.sequences, kmerSize);
        GridPane distanceGrid = this.view.getController().getDistancesOutputGridPane();
        distanceGrid.getChildren().clear();

        int length = headers.size();
        // fill headers in grid
        for (int i = 1; i <= length; i++){
            //fill first row with headers of sequences
            addTextfieldToGrid(distanceGrid, headers.get(i-1), i, 0);
            //fill first col with headers of sequences
            addTextfieldToGrid(distanceGrid, headers.get(i-1), 0, i);
        }

        //fill distance values in grid
        for (int j = 0; j < length; j++){           //row counter
            for (int k = 0; k < length; k++) {      //col counter
                TextField distance = new TextField(String.valueOf(distanceMatrix[j][k]));
                distance.setEditable(false);
                distanceGrid.add(distance, k+1, j+1);
            }
        }
    }

    /**
     * Add a text field to a GridPane
     * @param text - Text of the TextField to be added as String
     * @param col - column where the text field shall be added
     * @param row - row in which the text field shall be added
     */
    private void addTextfieldToGrid(GridPane grid, String text, int col, int row){
        TextField txt = new TextField(text);
        txt.setEditable(false);
        grid.add(txt, col, row);
    }

    /**
     * Update the outputArea of the sequence tab according to the current selection of header Checkbox and sequence CheckBox.
     */
    private void updateSequenceOutputArea() {
        CheckBox headerCB = this.view.getController().getHeadersCheckBox();
        CheckBox sequenceCB = this.view.getController().getSequencesCheckBox();
        this.view.getController().getTextOutputArea().setText(this.sequences.getText(headerCB.isSelected(), sequenceCB.isSelected()));
        updateStatusLine();
    }

    private void updateStatusLine() {
        this.view.getController().getBottomLineTextField().setText("Number of Sequences: " + sequences.size());
    }

    /**
     * Create a file chooser, which allows to open FastA files.
     * @return the file which was chosen by the user using the file Chooser.
     */
    public File chooseFastAFile(){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("FastA", "*.fasta")
        );
        fileChooser.setTitle("Open a fastA File");
        File fastAFile = fileChooser.showOpenDialog(stage);
        return fastAFile;
    }

    public Stage createAboutStage(){
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About");
        TextArea aboutText = new TextArea(about);
        aboutText.setWrapText(true);
        aboutStage.setScene(new Scene(aboutText, 400, 250));
        return aboutStage;
    }

}
