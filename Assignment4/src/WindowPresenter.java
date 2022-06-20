import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.InvalidValueException;
import model.PhyloTree;
import view.*;

import java.io.*;


/**
 * Class WindowPresenter can be used to communicate between view and model in the model-view-presenter pattern.
 * This WindowPresenter class realizes also the Visitor design pattern to provide methods for computation of a tree traversal.
 */
public class WindowPresenter {

    private final Window view;
    private Reader reader; //Tree fileReader

    private BooleanProperty endsWithSemicolon;

    private PhyloTree tree;

    /**
     * Constructor of the WindowPresenter class
     * @param view - the view to communicate with to update the GUI
     */
    public WindowPresenter(Window view){
        if(view != null){
            this.view = view;
            this.endsWithSemicolon = new SimpleBooleanProperty(false);

            createListener();

            // disable parse button when last char in text area is no semicolon:
            this.view.getController().getParseButtonBTxtTab().disableProperty().bind(this.endsWithSemicolon.not());
        }
        else{
            this.view = new Window();
            System.err.println("Window was tried to be set to null, which is not possible. A default instance of Window was used instead.");
        }
    }

    private void createListener(){
        //open file menu item
        this.view.getController().getOpenFileMenuItem().setOnAction(a -> {
            File file = chooseNewickFile();
            if(file != null) {
                String newick = readNewickFile(file);
                try {
                        this.tree = new PhyloTree(newick);
                        //only update if tree can be created
                        this.view.getController().getOutputAreaCTxtTab().setText(newick);    //status line is updated automatically
                    } catch (InvalidValueException e) {
                        this.view.displayErrorMessage("Error creating tree", e.getMessage());
                    }
            }
            //else no file was selected --> nothing shall happen
        });

        // change Boolean Property endsWithSemicolon when textArea changes
        this.view.getController().getOutputAreaCTxtTab().textProperty().addListener((observable, oldValue, newValue) -> endsWithSemicolon.setValue(newValue.endsWith(";")));

        // update statusLine when apply button was fired
        this.view.getController().getParseButtonBTxtTab().setOnAction(a -> this.view.getController().getCharLabelBTxtTab().textProperty().setValue(calculateStatusLine()));
    }

    /**
     * Read a file containing newick specification and provide the content of that file as one string
     * @param newickFile - the file to be read
     * @return the content of newickFile as one string
     *          null if newickFile is null
     */
    private String readNewickFile(File newickFile) {
        if(newickFile != null) {
            try {
                this.reader = new BufferedReader(new FileReader(newickFile));
                String newick = "";
                String line = ((BufferedReader) reader).readLine();
                while (line != null) {
                    newick += line;
                    line = ((BufferedReader) reader).readLine();
                }
                return newick;
            } catch (IOException ioException) {
                this.view.displayErrorMessage("Error reading newick file", ioException.getMessage());
            }
        }
        return null;
    }

    /**
     * Create a file chooser, which allows to open files specifying a tree.
     * @return the file which was chosen by the user using the file Chooser.
     */
    public File chooseNewickFile(){
        Stage stage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tree", "*.tre")
        );
        fileChooser.setTitle("Open a file containing newick format");
        File newickFile = fileChooser.showOpenDialog(stage);
        return newickFile;
    }


    private String calculateStatusLine() {
        try {
            int nodes = tree.getNodeNumber();
            int edges = nodes - 1;
            int leaves = tree.getLeaveNodeNumber();
            String binarity = tree.isBinary() ? " binary " : " non-binary ";
            double totalLength = tree.getTotalLength();

            String newStatusLine = "Nodes: " + nodes + " Edges: " + edges + " Leaves: " + leaves + binarity + " Total length: " + totalLength;
            return newStatusLine;
        }
        catch (InvalidValueException e){
            this.view.displayErrorMessage("Error computing status", e.getMessage());
        }
        return "";
    }

    public void createTree(String newickString){
        try{
            this.tree = new PhyloTree(newickString);
        }
        catch (InvalidValueException i){
            this.view.displayErrorMessage("Error creating tree", i.getMessage());
        }

    }
}
