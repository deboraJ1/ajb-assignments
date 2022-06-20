import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;

import model.InvalidValueException;
import model.PhyloTree;
import model.TreeEmbedder;
import model.TreeNode;
import view.MouseInteraction;
import view.Window;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Class WindowPresenter can be used to communicate between view and model in the model-view-presenter pattern.
 * This WindowPresenter class realizes also the Visitor design pattern to provide methods for computation of a tree traversal.
 */
public class WindowPresenter {

    private final Window view;
    private Reader reader; //Tree fileReader
    private final int offset = 10;

    private BooleanProperty endsWithSemicolon;
    private BooleanProperty isHoldingTree;
    private PhyloTree tree;

    //Groups of visual elements:
    // -- nucleoteds holds many subgroups of circles and labels (one per nucleotide)
    // -- lines holds all the lines between nucleotides
    private Group nucleotides, lines;

    //don't just change order of this lists values --> are referenced in switch case in method drawTree()
    private static final List<String> lineStyles = new ArrayList<>(){{ add("Straight"); add("Angular"); add("Quad"); add("Cubic");}};
    /**
     * Constructor of the WindowPresenter class
     * @param view - the view to communicate with to update the GUI
     */
    public WindowPresenter(Window view){
        if(view != null){
            this.view = view;
            this.endsWithSemicolon = new SimpleBooleanProperty(false);
            this.isHoldingTree = new SimpleBooleanProperty(false);

            //fill ChoiceBox
            this.view.getController().getChoiceBoxTBorderPane().setItems(FXCollections.observableArrayList(lineStyles));
            this.view.getController().getChoiceBoxTBorderPane().setValue(lineStyles.get(0));

            // create groups
            this.nucleotides = new Group();
            this.lines = new Group();

            createListener();
            createBindings();
        }
        else{
            this.view = new Window();
            System.err.println("Window was tried to be set to null, which is not possible. A default instance of Window was used instead.");
        }
    }

    /**
     * Create Bindings for control elements where model and more complicated logic is involved.
     */
    private void createBindings() {
        // disable parse button when last char in text area is no semicolon:
        this.view.getController().getParseButtonBTxtTab().disableProperty().bind(this.endsWithSemicolon.not());

        // enable controlls in tree tab only when tree is set/calculated
        this.view.getController().getChoiceBoxTBorderPane().disableProperty().bind(this.isHoldingTree.not());
        this.view.getController().getScaleCheckBoxRBorderPane().disableProperty().bind(this.isHoldingTree.not());
        this.view.getController().getDrawButtonBBorderPane().disableProperty().bind(this.isHoldingTree.not());
    }

    /**
     * Create the listener for file menu items, apply button and for changes in textArea
     */
    private void createListener(){
        //open file menu item
        this.view.getController().getOpenFileMenuItem().setOnAction(a -> {
            File file = chooseNewickFile();
            if(file != null) {
                String newick = readNewickFile(file);
                try {
                    this.tree = new PhyloTree(newick);
                    this.isHoldingTree.setValue(true);
                    //only update if tree can be created
                    this.view.getController().getOutputAreaCTxtTab().setText(newick);    //status line is updated automatically
                } catch (InvalidValueException e) {
                    this.isHoldingTree.setValue(false);
                    this.view.displayErrorMessage("Error creating tree", e.getMessage());
                }
            }
            //else no file was selected --> nothing shall happen
        });

        // change Boolean Property endsWithSemicolon when textArea changes
        this.view.getController().getOutputAreaCTxtTab().textProperty().addListener((observable, oldValue, newValue) -> endsWithSemicolon.setValue(newValue.endsWith(";")));

        // update statusLine when apply button was fired
        this.view.getController().getParseButtonBTxtTab().setOnAction(a -> updateStatusLine());

        // draw a tree
        this.view.getController().getDrawButtonBBorderPane().setOnAction(a -> drawTree());

        // clear menu item and clear Button with same functionality
        this.view.getController().getClearMenuItem().setOnAction(a -> resetModelView());
        this.view.getController().getClearButtonBTxtTab().setOnAction(a -> resetModelView());
    }

    /**
     * Clear the output area and reset view and model to the initial state.
     */
    private void resetModelView(){
        this.view.getController().getOutputAreaCTxtTab().setText(""); //clear output Area
        this.view.getController().getDrawPaneScrollPaneC().getChildren().clear();
        this.isHoldingTree.setValue(false);                           //disables contols in tree tab
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
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.dir") + "\\Assignment5\\resources")
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Tree", "*.tre")
        );
        fileChooser.setTitle("Open a file containing newick format");
        File newickFile = fileChooser.showOpenDialog(this.view.getRoot().getScene().getWindow());
        return newickFile;
    }

    /**
     * Update information to be displayed in the status line:
     * number of nodes (1), edges (2) and leaves (3), if tree is binary (4) and total edge length (5)
     */
    private void updateStatusLine() {
        int nodes = tree.getNodeNumber();
        this.view.getController().getNodesStatusLabelFP().textProperty().setValue(String.valueOf(nodes));
        //in a tree there is always one edge less than nodes.
        this.view.getController().getEdgesStatusLabelFP().textProperty().setValue(String.valueOf(nodes -1));

        this.view.getController().getLeavesStatusLabelFP().textProperty().setValue(String.valueOf(tree.getLeaveNodeNumber()));

        String binarity = tree.isBinary() ? " binary " : " non-binary ";
        this.view.getController().getBinaryStatusLabelFP().textProperty().setValue(String.valueOf(binarity));

        this.view.getController().getTotalLengthStatusLabelFP().textProperty().setValue(String.valueOf(tree.getTotalLength()));
    }

    /**
     * Draw all nodes of the tree in the model, if the tree is not null.
     * Label are created for nodes according to the passed newick format.
     * Lines are drawn according to the current selection of the choicebox in the tree tab.
     */
    private void drawTree() {
        if(this.tree != null) {
            Map<TreeNode, Point2D> node2pointMapping = TreeEmbedder.computeEmbedding(this.tree, this.view.getController().getScaleCheckBoxRBorderPane().isSelected(), 400, 400);

            if (node2pointMapping != null && node2pointMapping.size() > 0) {
                this.view.getController().getDrawPaneScrollPaneC().getChildren().clear();
                nucleotides.getChildren().clear(); //clear first, then add all nodes from current tree
                lines.getChildren().clear();
                try {
                    //start recursion
                    draw(null, this.tree.getRoot(), node2pointMapping);
                    this.view.getController().getDrawPaneScrollPaneC().getChildren().addAll(lines, nucleotides);
                } catch (InvalidValueException e) {
                    this.view.displayErrorMessage("Error drawing tree", e.getMessage());
                }
            } else {
                // this might not be an error, therefore no error message is displayed but just a log in terminal.
                System.out.println("There were no nucleotides to draw.");
            }
        }
    }

    /**
     * Draw all TreeNodes of a phylogenetic tree in a recursive way.
     * TreeNodes are represented as Circles and Labels containing the text of the according TreeNode. Each Circle representing a TreeNode is connected to the Circles of parent and children.
     * All circles will be shown according to the coordinates given in node2PointMapping and MouseInteraction is provided for the circles.
     * @param parent - parent node of child, shall be connected to child with a line, can be null if child is a root node.
     * @param child - TreeNode to be represented as Circle; shall not be null
     * @param node2pointMapping - Map providing a mapping for nodes to 2D points. Shall not be null and shall have a size > 0.
     * @throws InvalidValueException - if child is null or if node2pointMapping is null or has a size <= 0.
     */
    private void draw(Circle parent, TreeNode child, Map<TreeNode, Point2D> node2pointMapping) throws InvalidValueException{
        //parent can be null for root elements.
        if (child != null) {
            if (node2pointMapping != null && node2pointMapping.size() > 0) {
                //get position of this child node
                Point2D childPosition = node2pointMapping.get(child);

                //create circle and label of child and group those together
                Circle childCircle = createCircle(childPosition);
                Group childAndLabel = bindLabelToNucleotide(child.getLabel(), childCircle);
                this.nucleotides.getChildren().add(childAndLabel);

                if(parent != null) {
                    //create a line between parent and child circle, which uses bindings to stay updated
                    Shape connectionLine = createBoundedLine(parent, childCircle);
                    this.lines.getChildren().add(connectionLine);
                }
                //recursive call / tree traversal
                for (TreeNode grandchild : child.getChildren()) {
                    draw(childCircle, grandchild, node2pointMapping);
                }
            }
            else{
                throw new InvalidValueException("Nodes can not be drawn if the map of according points is null or has a size <= 0.");
            }
        }
        else{
            throw new InvalidValueException("Nucleotide of a TreeNode = null can not be drawn.");
        }
    }

    /**
     * Create a Circle object at the given position in 2D coordinates, which is able to handle mouse interaciton
     * @param position - position, where the center of the circle is supposed to be after creation. Shall not be null.
     * @return - Circle Object with default filling, stroke and radius and its center at position, can handle mouse interaction.
     * @throws InvalidValueException - if position is null
     */
    private Circle createCircle(Point2D position) throws InvalidValueException{
        if(position != null) {
            Circle nodeCircle = new Circle(0, 0, 10);
            nodeCircle.setFill(Color.WHITE);
            nodeCircle.setStroke(Color.BLACK);

            //translate to correct position
            nodeCircle.setTranslateX(position.getX() + offset);
            nodeCircle.setTranslateY(position.getY() + offset);

            //install MouseInteraction:
            MouseInteraction.install(nodeCircle, nodeCircle.translateXProperty(), nodeCircle.translateYProperty());
            return nodeCircle;
        }
        else{
            throw new InvalidValueException("Error when drawing nucleotides. Nucleotides position can never be null. Please check the tree you provided.");
        }
    }

    /**
     * Bind a label with text 'label' to the given Circle.
     * @param label - text of the label to be created.
     * @param nodeCircle - circle to be associated with label.
     * @return the group of a label with text 'label' and nodeCircle
     * @throws InvalidValueException - if label is null or nodeCircle is null.
     */
    private Group bindLabelToNucleotide(String label, Circle nodeCircle) throws InvalidValueException {
        if (label != null && nodeCircle != null) {
            //create subgroup with label
            Group nodeAndLabel = new Group();
            //label might even be blank
            Text nodeLabel = new Text(label);
            nodeLabel.setMouseTransparent(true);

            //bind label to nodeCircle position
            nodeLabel.translateXProperty().bind(nodeCircle.translateXProperty().add(nodeCircle.radiusProperty()).add(5));
            nodeLabel.translateYProperty().bind(nodeCircle.translateYProperty().add(nodeCircle.radiusProperty()).add(5));

            nodeAndLabel.getChildren().addAll(nodeCircle, nodeLabel);
            return nodeAndLabel;
        }
        else{
            throw new InvalidValueException("Label " + label + " and Circle " + nodeCircle + " can only be bound together if both are not null and label is not blank.");
        }
    }

    /**
     * Create a line between parent and child according to the current selection of the choicebox for line stypes.
     * @param parent - Parent circle, from where the line shall start
     * @param child - child circle, to which the line shall go
     * @return the line to be drawn as a shape object. Depending on the Selection of the checkbox, the Shape might be a different object inheriting from Shape.
     */
    private Shape createBoundedLine(Circle parent, Circle child) throws InvalidValueException{
        String lineType = this.view.getController().getChoiceBoxTBorderPane().getValue();
        if(lineType != null) {
            if (parent != null && child != null) {
                double parentX = parent.getCenterX();
                double parentY = parent.getCenterY();

                double childX = child.getCenterX();
                double childY = child.getCenterY();

                switch (lineStyles.indexOf(lineType)) {
                    case (0):
                        //Straight lines
                        Line straight = new Line(parentX, parentY, childX, childY);

                        straight.startXProperty().bind(parent.translateXProperty());
                        straight.startYProperty().bind(parent.translateYProperty());
                        straight.endXProperty().bind(child.translateXProperty());
                        straight.endYProperty().bind(child.translateYProperty());

                        straight.setStroke(Color.BLACK);
                        return straight;

                    case (1):
                        //angular lines
                        Line vertical = new Line(parentX, parentY, parentX, childY);
                        vertical.setStrokeWidth(2);
                        vertical.setFill(Color.BLACK);

                        vertical.startXProperty().bind(parent.centerXProperty());
                        vertical.startYProperty().bind(parent.centerYProperty());
                        vertical.endXProperty().bind(parent.centerXProperty());
                        vertical.endYProperty().bind(child.centerYProperty());

                        Line horizontal = new Line(parentX, childY, childX, childY);
                        horizontal.setStrokeWidth(2);
                        horizontal.setFill(Color.BLACK);

                        horizontal.startXProperty().bind(parent.centerXProperty());
                        horizontal.startYProperty().bind(child.centerYProperty());
                        horizontal.endXProperty().bind(child.centerXProperty());
                        horizontal.endYProperty().bind(child.centerYProperty());


                        /*MoveTo start = new MoveTo(parentX, parentY);
                        VLineTo angular_vert = new VLineTo(childY);
                        HLineTo angular_hor = new HLineTo(childX);

                        //Polyline angular = new Polyline(parentX, parentY, parentX, childY, childX, childY); //Dear Caner, PolyLine was not possible to be displayed.
                        //LineTo angular_vert = new LineTo(parentX, childY);                                  // this became very crazy drawing and even the actual version is not nice, but I dont understand why all lines start at the very left side...
                        //LineTo angular_hor = new LineTo(childX, childY);

                        angular_vert.yProperty().bind(child.translateYProperty());
                        angular_hor.xProperty().bind(child.translateXProperty());

                        Path angular = new Path(start, angular_vert, angular_hor);
                        angular.setVisible(true);
                        angular.setStroke(Color.BLACK);*/
                        return null;

                    case (2):
                        //quadratic lines
                        QuadCurve quad = new QuadCurve(parentX, parentY, parentX, childY, childX, childY);

                        //bindings
                        quad.startXProperty().bind(parent.translateXProperty());
                        quad.startYProperty().bind(parent.translateYProperty());

                        quad.controlXProperty().bind(quad.startXProperty());
                        quad.controlYProperty().bind(quad.endYProperty());

                        quad.endXProperty().bind(child.translateXProperty());
                        quad.endYProperty().bind(child.translateYProperty());

                        quad.setStroke(Color.BLACK);
                        quad.setFill(Color.TRANSPARENT);
                        return quad;

                    case (3):
                        //cubic lines
                        double middleX = parentX - childX;
                        CubicCurve cubic = new CubicCurve(parentX, parentY, middleX, parentY, middleX, childY, childX, childY);

                        //bindings
                        cubic.startXProperty().bind(parent.translateXProperty());
                        cubic.startYProperty().bind(parent.translateYProperty());

                        cubic.controlX1Property().bind(cubic.startXProperty().add(cubic.endXProperty().subtract(cubic.startXProperty()).divide(2)));
                        cubic.controlY1Property().bind(cubic.startYProperty());

                        cubic.controlX2Property().bind(cubic.startXProperty().add(cubic.endXProperty().subtract(cubic.startXProperty()).divide(2)));
                        cubic.controlY2Property().bind(cubic.endYProperty());

                        cubic.endXProperty().bind(child.translateXProperty());
                        cubic.endYProperty().bind(child.translateYProperty());

                        cubic.setStroke(Color.BLACK);
                        cubic.setFill(Color.TRANSPARENT);
                        return cubic;

                    default:
                        throw new InvalidValueException("Selection of line type " + lineType +" is not supported.");
                }
            }
            else{
                throw new InvalidValueException("Lines can not be drawn between circles which are null.");
            }
        }
        else{
            throw new InvalidValueException("Line type is never supposed to be null. Please choose a line type from the choice box in the tree tab.");
        }
    }
}
