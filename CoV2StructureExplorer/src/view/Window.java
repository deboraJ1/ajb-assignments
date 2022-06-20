package view;

import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.scene.*;
import javafx.scene.control.TextArea;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.biojava.nbio.structure.Structure;
import org.biojava.nbio.structure.StructureIO;
import org.biojava.nbio.structure.gui.BiojavaJmol;
import view.helper.Shape3DUtils;
import view.helper.WindowConstants;

import java.util.*;

/**
 * This is an active Window, which extends AbstractWindow.
 * Active means, that functionality concerning only the view is handled directly in the Window class, like displaying About text
 */
public class Window extends AbstractWindow{
    private final VBox root;
    private final Property<Transform> figureTransformProperty = new SimpleObjectProperty<>(new Rotate());

    //to show molecule
    private Group allSpahesGroup, moleculeGroup, polymersRibbonMeshes, rectangleGroup;// contain 3D objects
    private RotateTransition animation;

    private PerspectiveCamera moleculeCamera;
    private SubScene cameraScene;

    private StringProperty moleculeInfo;
    private final BooleanProperty isAnimationRunningProperty;
    private final BooleanProperty ribbonsAreVisible;
    private HashMap<String, List<AtomSphere>> spheresReferences;         //keep references to all groups which might be needed: Nucleotides, Helix, Sheets
    private HashMap<String, List<Cylinder>> polymersCylinder;

    public Window(){
        super(); //contains WindowController
        this.root = super.getController().getRoot();

        this.allSpahesGroup = new Group();
        this.moleculeGroup = new Group();
        this.polymersRibbonMeshes = new Group();

        resetMoleculeView();
        initViewElements();
        initCamera();
        initReferenceMap();

        //create Mouse interaction, bindings, listener
        MouseInteraction.installRotate(this.getController().getMoleculeViewPane(), this.figureTransformProperty);
        this.isAnimationRunningProperty = new SimpleBooleanProperty(false);
        this.ribbonsAreVisible = new SimpleBooleanProperty(false);

        addListener();
        addBindings();
    }

    /**
     * init the perspective camera for the subscene displaying the molecules.
     */
    private void initCamera() {
        this.moleculeCamera = new PerspectiveCamera(true);
        this.moleculeCamera.setFarClip(10000);
        this.moleculeCamera.setNearClip(0.1);
        //to see molecule in middle of window
        this.moleculeCamera.setTranslateZ(-400); //to ensure all objects are seen
        this.moleculeCamera.setFocusTraversable(true);

        this.cameraScene = new SubScene(allSpahesGroup, 800, 800, true, SceneAntialiasing.BALANCED);
        this.cameraScene.setVisible(true);
        this.cameraScene.setCamera(moleculeCamera);
        this.getController().getMoleculeViewPane().getChildren().add(cameraScene);
    }

    /**
     * init control elements of the view, so that consistent values are shown for e.g. slider as well as groups for visualization.
     */
    private void initViewElements(){
        this.moleculeInfo = new SimpleStringProperty();
        //default values of slider should be in middle to make molecule smaller and bigger.
        this.getController().getMoleculeSizeSlider().setValue(50);
        this.getController().getBondsSizeSlider().setValue(50);

        this.getController().getAtomChoiceBox().setItems(FXCollections.observableArrayList(WindowConstants.getAtoms()));
        this.getController().getAtomChoiceBox().setTooltip(new Tooltip("Choose atoms to be displayed"));
        this.getController().getStyleComboBox().setItems(FXCollections.observableArrayList(WindowConstants.getDrawingStyle()));
        this.getController().getStyleComboBox().setTooltip(new Tooltip("Select a drawing style"));
        this.getController().getColoringComboBox().setItems(FXCollections.observableArrayList(WindowConstants.getColoring()));
        this.getController().getColoringComboBox().setTooltip(new Tooltip("Select a coloring criteria"));

        this.animation = new RotateTransition();
        this.animation.setDuration(Duration.seconds(3));
        this.animation.setInterpolator(Interpolator.LINEAR);
        this.animation.setAxis(Shape3DUtils.YAXIS);
        this.animation.setFromAngle(0);
        this.animation.setToAngle(360);
        this.animation.setCycleCount(RotateTransition.INDEFINITE);
    }

    /**
     * Init the reference Map which generic groups (in contrast to groups of concret chains which can only be created later.
     * This Map is used to keep track of which sphere in the scene belongs to which group of helix, sheet, nucleotide or other.
     */
    private void initReferenceMap() {
        this.spheresReferences = new HashMap<>();
        this.spheresReferences.put("HELIX", new ArrayList<>());
        this.spheresReferences.put("SHEET", new ArrayList<>());
        this.spheresReferences.put("NUCLEOTIDE", new ArrayList<>());
        this.spheresReferences.put("OTHER", new ArrayList<>());
        this.polymersCylinder = new HashMap<>();
    }

    /**
     * reset all groups storing elements of the molecules in the view.
     * This method shall be called as preparation for another molecule, which shall be drawn of for clearing the current views molecule.
     */
    public void resetMoleculeView(){
        if(this.cameraScene != null){this.cameraScene.setRoot(this.allSpahesGroup);} //to clear stage, but when constructing, this is still null and not needed.
        if(this.allSpahesGroup.getChildren().size() != 0){
            this.allSpahesGroup.getChildren().clear();
        }
        this.allSpahesGroup.getChildren().addAll(this.moleculeGroup, this.polymersRibbonMeshes);

        this.getController().getMoleculeSizeSlider().setValue(50);
        this.getController().getBondsSizeSlider().setValue(50);
        this.getController().getColoringComboBox().getSelectionModel().select(0);
        this.getController().getStyleComboBox().getSelectionModel().select(0);
        this.getController().getAtomChoiceBox().getSelectionModel().select(0);

        this.getController().getShowBallsCheckbox().setSelected(true);
        this.getController().getShowBondsCheckbox().setSelected(true);

        this.rectangleGroup = new Group();
        this.getController().getSelectionPane().getChildren().add(rectangleGroup);
    }

    /**
     * Create Bindings of Control elements which only affect the view.
     */
    private void addBindings(){
        //enable sliders only when at least one of balls or bonds is visible
        this.getController().getMoleculeSizeSlider().disableProperty().bind(this.getController().getShowBallsCheckbox().selectedProperty().not());
        this.getController().getBondsSizeSlider().disableProperty().bind(this.getController().getShowBondsCheckbox().selectedProperty().not());

        //bind cameraScene size to top scene size
        this.cameraScene.widthProperty().bind(this.getController().getMoleculeViewPane().widthProperty());
        this.cameraScene.heightProperty().bind(this.getController().getMoleculeViewPane().heightProperty());

        this.getController().getStatusLabelB().textProperty().bind(moleculeInfo);

        this.polymersRibbonMeshes.visibleProperty().bind(this.ribbonsAreVisible);
        this.moleculeGroup.visibleProperty().bind(this.ribbonsAreVisible.not());
    }

    /**
     * Adds all EventHandler to Control elements where no interaction with WindowPresenter is needed.
     */
    private void addListener() {
        //listener to show about stage
        getController().getAboutMI().setOnAction(a -> createAboutStage().show());
        //listener to show BioJava stage
        getController().getShowBiojava().setOnAction(a -> showBioJavaPane());
        getController().getBioJavaButton().setOnAction(a -> showBioJavaPane());
        //dark theme
        this.getController().getDarkThemeMI().selectedProperty().addListener((v, o, n) -> {
            if(n){
                this.getRoot().getScene().getStylesheets().add("view/css/mondena_dark.css");
            }
            else{
                this.getRoot().getScene().getStylesheets().remove("view/css/mondena_dark.css");
            }
        });
        //listener to close application
        getController().getCloseMI().setOnAction(a -> System.exit(0));

        // listener for mouse scrolling
        this.getController().getMoleculeViewPane().setOnScroll(s -> moleculeCamera.setTranslateZ(moleculeCamera.getTranslateZ() + s.getDeltaY()));
        //listen for rotation
        this.figureTransformProperty.addListener((v, o, n) -> {
            if(this.ribbonsAreVisible.get()){
                this.polymersRibbonMeshes.getTransforms().setAll(n);
            }else{
                this.moleculeGroup.getTransforms().setAll(n);
            }
        });

        //________________________________________________________________________________Control Elements on right side
        //animation
        this.getController().getAnimationButton().setOnAction(a -> {
            if(this.isAnimationRunningProperty.get()){
                //stop animation
                this.animation.stop();
                this.getController().getAnimationButton().setText("Start Animation");
                this.isAnimationRunningProperty.set(false);
            }
            else{
                //start animation
                this.animation.setNode(this.moleculeGroup);
                this.animation.play();
                this.getController().getAnimationButton().setText("Stop Animation");
                this.isAnimationRunningProperty.set(true);
            }
        });
    }

    /**
     * create a new Stage to display the about text.
     * @return Stage to be displayed.
     */
    private Stage createAboutStage(){
        Stage aboutStage = new Stage();
        aboutStage.setTitle("About");
        TextArea aboutText = new TextArea(WindowConstants.getAbout());
        aboutText.setWrapText(true);
        aboutStage.setScene(new Scene(aboutText, 400, 250));
        return aboutStage;
    }


    //___________________________________________________________________________________________________SPHERE COLORING
    /**
     * Draw all given spheres in the provided color
     * @param spheres list of spheres which shall be shown in same color
     * @param color the color which all spheres material shall have.
     */
    private void drawSpheresInColor(List<AtomSphere> spheres, Color color){
        if(spheres != null && spheres.size() > 0 && color != null){
            for(AtomSphere s : spheres){
                s.setColor(color);
            }
        }
    }

    /**
     * Draw all spheres in the scene in their color according to which atom they represent.
     */
    public void drawSpheresInAtomColor(){
        List<Node> polyGroups = this.moleculeGroup.getChildren();
        if(polyGroups.size() > 0) {
            for (Node polyGroup : polyGroups) {
                if (polyGroup.getClass().equals(Group.class)) {//it is safe to cast
                    List<Node> structGroups = ((Group) polyGroup).getChildren();
                    for (Node structGroup : structGroups) {
                        if (structGroup.getClass().equals(Group.class)) {//it is safe to cast
                            List<Node> monoGroups = ((Group) structGroup).getChildren();
                            for (Node monomerGroup : monoGroups) {
                                if (monomerGroup.getClass().equals(Group.class)) {//it is safe to cast
                                    List<Node> single3DobjectsGroup = ((Group) monomerGroup).getChildren();
                                    for (Node objectGroup : single3DobjectsGroup) {
                                        if (objectGroup.getClass().equals(Group.class)) {//it is safe to cast
                                            Group atomGroup = (Group) ((Group) monomerGroup).getChildren().get(0);
                                            for (Node sphere : atomGroup.getChildren()) {
                                                if (sphere.getClass().equals(AtomSphere.class)) {
                                                    ((AtomSphere) sphere).setColor(((AtomSphere) sphere).originalAtomColor);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Colors all Spheres in one color. This method should be called before any other selection because otherwise
     * all spheres that dont appear in next coloring mode would be confusing to see the new coloring well.
     */
    public void colorAllSpheresInLightBlack(){
        List<Node> polyGroups = this.moleculeGroup.getChildren();
        Color c = new Color(1, 1, 1, 0.1);
        if(polyGroups.size() > 0) {
            for (Node polyGroup : polyGroups) {
                if (polyGroup.getClass().equals(Group.class)) {//it is safe to cast
                    List<Node> structGroups = ((Group) polyGroup).getChildren();
                    for (Node structGroup : structGroups) {
                        if (structGroup.getClass().equals(Group.class)) {//it is safe to cast
                            List<Node> monoGroups = ((Group) structGroup).getChildren();
                            for (Node monomerGroup : monoGroups) {
                                if (monomerGroup.getClass().equals(Group.class)) {//it is safe to cast
                                    List<Node> single3DobjectsGroup = ((Group) monomerGroup).getChildren();
                                    for (Node objectGroup : single3DobjectsGroup) {
                                        if (objectGroup.getClass().equals(Group.class)) {//it is safe to cast
                                            Group atomGroup = (Group) ((Group) monomerGroup).getChildren().get(0);
                                            for (Node sphere : atomGroup.getChildren()) {
                                                if (sphere.getClass().equals(AtomSphere.class)) {
                                                    ((AtomSphere) sphere).setColor(c);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Draw all spheres of one Group in the same color.
     * Each group is specified by a name, which can be the Polymer / Chain ID, Structure ID or Monomer ID
     * Each of those groups will have a randomly generated color assigned. All Spheres which do not belong to any mentioned group will have the same color.
     * @param sheetNames specifies the different groups of the Molecules, which shall be shown in different colors.
     */
    public void drawSpheresForGrouping(Set<String> sheetNames){
        if(sheetNames != null) {
            if(sheetNames.size() > 0) {
                colorAllSpheresInLightBlack();
                for (String name : sheetNames) {
                    List<AtomSphere> moleculesSpheres = spheresReferences.get(name);
                    drawSpheresInColor(moleculesSpheres, createRandomColor());
                }
            }
            else{
                this.setMoleculeInfo("This type is not appearing in the current Molecules.");
            }
        }
    }

    /**
     * Draw all spheres in the scene in rainbow colors where spheres in same x sub range have the same color.
     * y and z positions are not considered. The total x range of the molecules will be divided into 6 sub ranges in which all spheres have the same color.
     * @param minX the minimum x value within the molecules for any atoms location. Needs to be smaller than maxX
     * @param maxX the maximum x value within the molecules for any atoms location. Needs to be greater than minX
     */
    public void drawSpheresInRainbow(double minX, double maxX){
        Color[] rainbowColors = WindowConstants.getRainbowColors();
        int rainbowColorNumber = rainbowColors.length;
        double rangeWidth = (maxX - minX)/rainbowColorNumber;

        List<Node> polyGroups = this.moleculeGroup.getChildren();
        if(polyGroups.size() > 0) {
            for (Node polyGroup : polyGroups) {
                if (polyGroup.getClass().equals(Group.class)) {//it is safe to cast
                    List<Node> structGroups = ((Group) polyGroup).getChildren();
                    for (Node structGroup : structGroups) {
                        if (structGroup.getClass().equals(Group.class)) {//it is safe to cast
                            List<Node> monoGroups = ((Group) structGroup).getChildren();
                            for (Node monomerGroup : monoGroups) {
                                if (monomerGroup.getClass().equals(Group.class)) {//it is safe to cast
                                    List<Node> single3DobjectsGroup = ((Group) monomerGroup).getChildren();
                                    for (Node objectGroup : single3DobjectsGroup) {
                                        if (objectGroup.getClass().equals(Group.class)) {//it is safe to cast
                                            Group atomGroup = (Group) ((Group) monomerGroup).getChildren().get(0);
                                            for (Node sphere : atomGroup.getChildren()) {
                                                if (sphere.getClass().equals(AtomSphere.class)) {
                                                    double xVal = sphere.getTranslateX();
                                                    int color = Math.floorDiv((int) xVal, (int) rangeWidth) +2;
                                                    if(color < 0){
                                                        color = rainbowColorNumber + color;
                                                    }
                                                    else if(color >= rainbowColorNumber){
                                                        color -= rainbowColorNumber;
                                                    }
                                                    ((AtomSphere) sphere).setColor(rainbowColors[color]);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Create random R, G and B values which are then used to generate a random color.
     * @return a color object with random RGB values.
     */
    private Color createRandomColor(){
        Random rand = new Random();
        double r = rand.nextDouble();
        double g = rand.nextDouble();
        double b = rand.nextDouble();
        return Color.color(r, g, b);
    }


    /**
     * Shows a new Window with the structure of the selected file in an extra window.
     */
    private void showBioJavaPane() {
        String selectedEntryID = getController().getIdListView().getSelectionModel().selectedItemProperty().getValue();
        try {
            Structure struct = StructureIO.getStructure(selectedEntryID);
            BiojavaJmol panel = new BiojavaJmol();
            panel.setStructure(struct);

            // send some RASMOL style commands to Jmol
            panel.evalString("select * ; color chain;");
            panel.evalString("select *; spacefill off; wireframe off; backbone 0.4;  ");
        }
        catch (Exception e){
            this.displayErrorMessage("Error creating BioJava View", String.format("Tried to show Molecule with Entry ID %s", selectedEntryID) + "\n\n" + e.getMessage());
        }
    }

    //______________________________________________________________________________________________SOME GETTER / SETTER
    /**
     * set Molecule info to which status label is bound
     * @param info the info text which will then be shown in status label if info is not null
     */
    public void setMoleculeInfo(String info){
        if(info != null){
            this.moleculeInfo.set(info);
        }
    }
    public void setPolymersRibbonMeshGroup(Group ribbonMeshGroup){
        if(ribbonMeshGroup != null){
            this.polymersRibbonMeshes.getChildren().setAll(ribbonMeshGroup.getChildren());
        }
    }
    public void setSpheresReferences(HashMap<String, List<AtomSphere>> groupingReferences) {
        if(groupingReferences != null) {
            this.spheresReferences = groupingReferences;
        }
    }
    public void setPolymersCylinder(HashMap<String, List<Cylinder>> polymersCylinder) {
        if(polymersCylinder != null) {
            this.polymersCylinder = polymersCylinder;
        }
    }
    public void setMoleculeGroup(Group moleculeGroup) {
        if(moleculeGroup != null){
            this.moleculeGroup.getChildren().setAll(moleculeGroup.getChildren());
        }
    }
    public void setRibbonsAreVisible(boolean ribbonsAreVisible) {
        this.ribbonsAreVisible.set(ribbonsAreVisible);
        this.getController().getShowBallsCheckbox().setSelected(!ribbonsAreVisible);
        this.getController().getShowBondsCheckbox().setSelected(!ribbonsAreVisible);
    }

    public Parent getRoot() {
        return this.root;
    }
    public HashMap<String, List<AtomSphere>> getSpheresReferences() {
        return spheresReferences;
    }

    public HashMap<String, List<Cylinder>> getPolymersCylinder() {
        return polymersCylinder;
    }

    public Group getRectangleGroup(){return this.rectangleGroup;}
    public Property<Transform> figureTransformPropertyProperty() {
        return figureTransformProperty;
    }
    public Camera getMoleculeCamera(){
        return this.moleculeCamera;
    }

    public BooleanProperty ribbonsAreVisibleProperty() {
        return ribbonsAreVisible;
    }

    public BooleanProperty isAnimationRunningPropertyProperty() {
        return isAnimationRunningProperty;
    }
}
