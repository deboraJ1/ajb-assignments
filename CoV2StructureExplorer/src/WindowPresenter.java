import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.ParallelTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.*;
import javafx.collections.SetChangeListener;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape3D;
import javafx.stage.FileChooser;
import javafx.util.Duration;
import javafx.util.Pair;
import model.MoleculeAnalyzer;
import model.atoms.AtomI;
import model.molecules.Molecules;
import model.molecules.Monomer;
import model.molecules.Polymer;
import model.molecules.StructureType;
import model.pdbaccess.FileParser;
import model.pdbaccess.PDBWebClient;
import model.tasks.CreateMoleculeTask;
import model.tasks.ReadFileContentTask;
import model.tasks.SetupVisualizationTask;
import selection.AtomSelectionModel;
import selection.SelectionModel;
import selection.SelectionRectangles;
import view.AtomSphere;
import view.Window;
import view.undo.PropertyCommand;
import view.undo.SimpleCommand;
import view.undo.UndoRedoManager;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

/**
 * Class WindowPresenter can be used to communicate between view and model in the model-view-presenter pattern.
 */
public class WindowPresenter {

    private final Window view;
    private PDBWebClient pdbWebClient;
    private Molecules model;

    //services
    private final Service<String> readFileContentService;
    private final Service<Molecules> createMoleculeService;
    private final Service<HashMap<AtomI, AtomSphere>> setupMoleculeVisualizationService;

    //properties
    private ListProperty<String> visibleEntryIdListProperty;
    private final BooleanProperty notAllIDsAreShown;
    private final StringProperty currentFileStringProperty;           //String property of content shown in File text tab
    private final StringProperty selectedFileNameProperty;            //last selected filename
    private final BooleanProperty isExploding;

    //selection
    private final SelectionModel<AtomI> selectionModel;
    private final SelectionRectangles selectionRectangles;
    Function<AtomI, List<AtomSphere>> item2shapesFunction;
    private HashMap<AtomI, AtomSphere> atom2sphere;                   //view - model linkage

    private final UndoRedoManager undoRedoManager;

    public WindowPresenter(Window view, PDBWebClient pdbWebClient, FileParser fileParser) {
        if(view != null){
            this.view = view;
        }
        else{
            this.view = new Window();
            System.err.println("Window was tried to be set to null, which is not possible. A default instance of Window was used instead.");
        }
        if(pdbWebClient != null){
            this.pdbWebClient = pdbWebClient;
        }
        else {
            try {
                this.pdbWebClient = new PDBWebClient();
            }
            catch (IOException e) {
                //probably no internet and exception is already shown in ProjectMain.
                System.err.println("No Internet connection to PDB Server: " +  e.getMessage());
            }
        }
        try {
            assert pdbWebClient != null;
            this.visibleEntryIdListProperty = pdbWebClient.allEntryIDsProperty();
        } catch (IOException e) {
            System.err.println("Error requesting Entry IDs from PDB server" + e.getMessage());
        }

        //init windowPresenter properties
        this.currentFileStringProperty = new SimpleStringProperty();
        this.selectedFileNameProperty = new SimpleStringProperty();
        this.isExploding = new SimpleBooleanProperty(false);

        this.notAllIDsAreShown = new SimpleBooleanProperty(false); //in the beginning all entry ids are shown.

        this.undoRedoManager = new UndoRedoManager();
        this.selectionModel = new AtomSelectionModel();
        this.item2shapesFunction = atomI -> {
            List<AtomSphere> spheres = new ArrayList<>();
            spheres.add(atom2sphere.get(atomI));
            return spheres;
        };
        this.selectionRectangles = new SelectionRectangles(this.view.getController().getSelectionPane(), collectSelectionAffectingProperties());

        //createServices and Tasks
        this.readFileContentService = new Service<>() {
            @Override
            protected Task<String> createTask() {
                return new ReadFileContentTask(selectedFileNameProperty, pdbWebClient);
            }
        };
        this.createMoleculeService = new Service<>() {
            @Override
            protected Task<Molecules> createTask() {
                return new CreateMoleculeTask(selectedFileNameProperty, fileParser);
            }
        };
        this.setupMoleculeVisualizationService = new Service<>() {
            @Override
            protected Task<HashMap<AtomI, AtomSphere>> createTask() {
                return new SetupVisualizationTask(model, view, selectionModel);
            }
        };
        this.model = new Molecules("INIT MOLECULE");
        createListener();
        createBindings();
    }

    /**
     * Get all Properties which are affecting the position of the rectangles in the view
     * @return a List of Properties containing properties which are affecting where the rectangle shall be shown to be at the right sphere.
     */
    private List<Property> collectSelectionAffectingProperties() {
        List<Property> properties = new ArrayList<>();
        properties.add(this.view.getController().getMoleculeSizeSlider().valueProperty());
        properties.add(this.view.getController().getBondsSizeSlider().valueProperty());
        //properties.add(this.view.getController().getMoleculeViewPane().onMouseDraggedProperty());
        properties.add(this.view.getController().getAnimationButton().onMouseClickedProperty());
        properties.add(this.view.getController().getExplosionButton().onMouseClickedProperty());
        properties.add(this.view.figureTransformPropertyProperty());
        properties.add(this.view.getMoleculeCamera().translateZProperty());
        return properties;
    }

    /**
     * Create Bindings for control elements where model and more complicated logic is involved.
     */
    private void createBindings() {
        this.view.getController().getFileOutputTextArea().textProperty().bind(currentFileStringProperty);
        this.view.getController().getClearMI().disableProperty().bind(this.selectionModel.getSelectedItemsSizeProperty().lessThan(1));
        this.view.getController().getSelectMI().disableProperty().bind(this.view.ribbonsAreVisibleProperty());

        //bindings to disable control elements if no file was selected:
        this.view.getController().getShowBallsCheckbox().disableProperty().bind(currentFileStringProperty.isEmpty().or(currentFileStringProperty.isNull().or(this.view.ribbonsAreVisibleProperty())));
        this.view.getController().getShowBondsCheckbox().disableProperty().bind(this.view.getController().getShowBallsCheckbox().disableProperty().or(this.view.ribbonsAreVisibleProperty()));
        this.view.getController().getBioJavaButton().disableProperty().bind(currentFileStringProperty.isEmpty().or(currentFileStringProperty.isNull()));
        this.view.getController().getStyleComboBox().disableProperty().bind(currentFileStringProperty.isEmpty().or(currentFileStringProperty.isNull()));
        this.view.getController().getColoringComboBox().disableProperty().bind(currentFileStringProperty.isEmpty().or(currentFileStringProperty.isNull().or(this.view.ribbonsAreVisibleProperty())));
        this.view.getController().getAtomChoiceBox().disableProperty().bind(currentFileStringProperty.isEmpty().or(currentFileStringProperty.isNull().or(this.view.ribbonsAreVisibleProperty())));
        this.view.getController().getExplosionButton().disableProperty().bind(this.isExploding.or(this.view.ribbonsAreVisibleProperty()).or(currentFileStringProperty.isEmpty().or(currentFileStringProperty.isNull())).or(this.model.numberOfPolymersProperty().greaterThan(1)));
        this.view.getController().getAnimationButton().disableProperty().bind(currentFileStringProperty.isEmpty().or(currentFileStringProperty.isNull()));

        //List view bound to selected IDs
        this.view.getController().getIdListView().itemsProperty().bind(this.visibleEntryIdListProperty);
        //show all or only sarsCov enabled depending on what is shown
        this.view.getController().getListAllButton().disableProperty().bind(this.notAllIDsAreShown.not());
        this.view.getController().getSarsCovButton().disableProperty().bind(this.notAllIDsAreShown);

        //bind visibility of slider if file was selected
        this.view.getController().getMoleculeSizeSlider().disableProperty().bind(selectedFileNameProperty.isNull().or(selectedFileNameProperty.isEmpty().or(this.view.ribbonsAreVisibleProperty())));
        this.view.getController().getBondsSizeSlider().disableProperty().bind(selectedFileNameProperty.isNull().or(selectedFileNameProperty.isEmpty().or(this.view.ribbonsAreVisibleProperty())));

        //only enable list when no task is running or in progress (for reading file or creating molecule)
        this.view.getController().getIdListView().disableProperty().bind(readFileContentService.progressProperty().greaterThan(0).or(readFileContentService.runningProperty()).or(createMoleculeService.progressProperty().greaterThan(0).or(createMoleculeService.runningProperty()).or(setupMoleculeVisualizationService.progressProperty().greaterThan(0).or(setupMoleculeVisualizationService.runningProperty()))));

        //visibility of progress bars and label if task is running and bind progress to task
        this.view.getController().getFileProgressBar().visibleProperty().bind(readFileContentService.runningProperty().or(readFileContentService.progressProperty().greaterThan(0)));
        this.view.getController().getFileProgressBarLabel().visibleProperty().bind(this.view.getController().getFileProgressBar().visibleProperty());
        this.view.getController().getFileProgressBar().progressProperty().bind(readFileContentService.progressProperty());

        this.view.getController().getMoleculeProgressBar().visibleProperty().bind(createMoleculeService.runningProperty().or(createMoleculeService.progressProperty().greaterThan(0)));
        this.view.getController().getMoleculeProgressBarLabel().visibleProperty().bind(this.view.getController().getMoleculeProgressBar().visibleProperty());
        this.view.getController().getMoleculeProgressBar().progressProperty().bind(createMoleculeService.progressProperty());

        this.view.getController().getSetupViewProgressBar().visibleProperty().bind(setupMoleculeVisualizationService.runningProperty().or(setupMoleculeVisualizationService.progressProperty().greaterThan(0)));
        this.view.getController().getSetupViewProgressBarLabel().visibleProperty().bind(this.view.getController().getSetupViewProgressBar().visibleProperty());
        this.view.getController().getSetupViewProgressBar().progressProperty().bind(setupMoleculeVisualizationService.progressProperty());
    }


    /**
     * Create the listener for view control elements, including menu items, selection, PDB entry IDs list, coloring of spheres
     * and listeners for redo manager and services.
     */
    private void createListener() {
        //____________________________________________________________________________________________MENU ITEM LISTENER
        //open file menu item (is in Window Presenter because file content might be requested from PDBWebClient)
        this.view.getController().getOpenMI().setOnAction(a -> {
            File file = chooseFile(FileParser.FILE_EXTENSION);
            //display selected file
            if (file != null) {
                String filename = file.getName();
                if (filename.endsWith(".pdb")) {
                    filename = filename.substring(0, filename.length() - 4);
                }
                this.view.getController().getIdListView().getSelectionModel().select(filename);
                this.selectionModel.clearSelection();
                this.readFileContentService.restart();
            }
            //else no file was selected --> nothing shall happen
        });
        this.view.getController().getClearMI().setOnAction(a -> {
            this.view.getRectangleGroup().getChildren().clear();
            this.view.setMoleculeInfo("No Atom is selected.");
        });
        this.view.getController().getSelectMI().setOnAction(a -> this.selectionModel.selectAll(this.model.getAtoms()));

        createUndoRedoManagerListener();

        //_____________________________________________________________________________________________________SELECTION
        this.selectionModel.getSelectedItems().addListener((SetChangeListener<AtomI>) c -> {
            if(this.view.getRectangleGroup() != null && this.view.getRectangleGroup().getChildren() != null) {
                String selectedText = generateSelectionInfoText();
                this.view.setMoleculeInfo(selectedText);
                if (c.wasRemoved()) {
                    Platform.runLater(() -> {
                        for (AtomSphere sphere : item2shapesFunction.apply(c.getElementRemoved())) {
                            this.view.getRectangleGroup().getChildren().remove(selectionRectangles.removeRectangleOfShape(sphere));
                        }
                    });
                }
                if (c.wasAdded()) {
                    Platform.runLater(() -> {
                        for (AtomSphere sphere : item2shapesFunction.apply(c.getElementAdded())) {
                            Rectangle rectangle = selectionRectangles.createBoundingBoxWithBinding(sphere);
                            this.view.getRectangleGroup().getChildren().add(rectangle);
                        }
                    });
                }
            }
            else{
                System.err.println("Rectangles can not be added or removed of selection if rectangles group is null or its children.");
            }
        });
        this.view.isAnimationRunningPropertyProperty().addListener((v, o, n) -> {if(n) selectionModel.clearSelection();});

        createIDListListener();
        createServiceListener();

        //_____________________________________________________________________________________________MOLECULE CONTROLS
        this.view.getController().getStyleComboBox().valueProperty().addListener((v, o, n) -> {
            if(n != null && n != o){
                this.selectionModel.clearSelection();
                this.undoRedoManager.clear();
                switch (n){
                    case "Balls and Sticks":
                        this.view.setRibbonsAreVisible(false);
                        break;
                    case "Ribbon":
                        this.view.setRibbonsAreVisible(true);
                        break;
                    default:
                        this.view.displayInfoMessage("Changing Drawing Style", "Sorry, it seems like a drawing style was selected, which is not yet supported by this program. Please report it.");
                }
            }
        });

        //coloring
        this.view.getController().getColoringComboBox().valueProperty().addListener((v, o, n) -> {
            StringBuilder type = new StringBuilder();
            int size = 0;
            Set<String> groupNames = null;
            switch (n.trim().toUpperCase()) {
                case "STRUCTURE TYPES" -> {
                    Set<StructureType> structures = this.model.getStructureTypes();
                    for (StructureType structType : structures) {
                        Set<String> typeNames = this.model.getStructureIDs(structType);
                        type.append(structType.name()).append(" ");
                        this.view.drawSpheresForGrouping(typeNames);
                    }
                }
                case "MONOMERS" -> {
                    groupNames = this.model.getMonomerNames();
                    type = new StringBuilder("Monomers");
                }
                case "NUCLEOTIDES" -> {
                    Set<String> monosToBeFilteredForNucleotides = this.model.getMonomerNames();
                    groupNames = new HashSet<>();
                    for (String monomerName : monosToBeFilteredForNucleotides) {
                        if (monomerName.length() < 3) {
                            groupNames.add(monomerName);
                        }
                    }
                    type = new StringBuilder("Nucleotides");
                }
                case "AMINO ACIDS" -> {
                    Set<String> monosToBeFilteredForAminos = this.model.getMonomerNames();
                    groupNames = new HashSet<>();
                    for (String monomerName : monosToBeFilteredForAminos) {
                        if (monomerName.length() == 3) {
                            groupNames.add(monomerName);
                        }
                    }
                    type = new StringBuilder("Amino Acids");
                }
                case "SHEETS" -> {
                    type = new StringBuilder("Sheets");
                    groupNames = this.model.getStructureIDs(StructureType.SHEET);
                }
                case "HELIX" -> {
                    type = new StringBuilder("Helix");
                    groupNames = this.model.getStructureIDs(StructureType.HELIX);
                }
                case "MOLECULES" -> {
                    type = new StringBuilder("Molecules");
                    groupNames = this.model.getChainNames();
                }
                case "RAINBOW" -> {
                    Pair<Point3D, Point3D> minMax = this.model.findRanges();
                    this.view.drawSpheresInRainbow(minMax.getKey().getX(), minMax.getValue().getX());
                }
                default -> {
                    //includes ATOMS
                    this.view.drawSpheresInAtomColor();
                    this.view.setMoleculeInfo("Molecules are drawn with their atoms colored in default colors.");
                }
            }
            if(groupNames != null) {
                this.view.drawSpheresForGrouping(groupNames);
                this.view.setMoleculeInfo(String.format("Molecule has %d different %s.", groupNames.size(), type));
            }
            else if(n.trim().equalsIgnoreCase("STRUCTURE TYPES")){
                this.view.setMoleculeInfo(String.format("Molecules have %d different structure types which are: %s", size, type));
            }
            //case rainbow or atoms/default applied
        });
        //explode
        this.view.getController().getExplosionButton().setOnAction(a ->{
            this.isExploding.set(true);
            this.selectionModel.clearSelection();
            this.undoRedoManager.clear();
            boolean bonsWereVisible = this.view.getController().getShowBondsCheckbox().selectedProperty().get();
            if(bonsWereVisible){this.view.getController().getShowBondsCheckbox().setSelected(false);}

            Set<String> polyNames = this.model.getChainNames();
            ParallelTransition allExplosions = new ParallelTransition();

            for(String polyName : polyNames){
                List<AtomSphere> polySpheres = this.view.getSpheresReferences().get(polyName);
                Point3D polyCenter = MoleculeAnalyzer.getCenterOfSubMolecules(polySpheres);
                for(AtomSphere sphere : polySpheres) {
                    Timeline sphereExploding = createExplosionKeyFrame(sphere, polyCenter.normalize());
                    allExplosions.getChildren().add(sphereExploding);
                }
                /* //this somehow did not seem to move the bonds.
                List<Cylinder> polyCylinders = this.view.getPolymersCylinder().get(polyName);
                for(Cylinder cylinder : polyCylinders){
                    Timeline cylinderExploding = createExplosionKeyFrame(cylinder, polyCenter.normalize());
                    allExplosions.getChildren().add(cylinderExploding);
                }*/
            }
            allExplosions.setOnFinished(e -> {
                if(bonsWereVisible){this.view.getController().getShowBondsCheckbox().setSelected(true);}
                this.isExploding.set(false);
            });
            allExplosions.play();
        });
    }

    /**
     * Create an Explosion Timeline which moves given shape by double the given translation
     * @param shape shape to be moved
     * @param translation translation vector, which is given as point. shape will be moved by twice its value.
     * @return the resulting Timeline for that shapes translation.
     */
    private Timeline createExplosionKeyFrame(Shape3D shape, Point3D translation){
        if(shape != null && translation != null) {
            KeyFrame explodeMove = new KeyFrame(Duration.seconds(3),
                    new KeyValue(shape.translateXProperty(), shape.translateXProperty().get() + 2*translation.getX()),
                    new KeyValue(shape.translateYProperty(), shape.translateYProperty().get() + 2*translation.getY()),
                    new KeyValue(shape.translateZProperty(), shape.translateZProperty().get() + 2*translation.getZ())
            );
            Timeline polymerTimeline = new Timeline(explodeMove);
            polymerTimeline.setAutoReverse(true);
            polymerTimeline.setCycleCount(2);
            return polymerTimeline;
        }
        return null;
    }

    /**
     * Create listener needed for the UndoRedoManager
     */
    private void createUndoRedoManagerListener() {
        this.view.getController().getUndoMI().setOnAction(a->undoRedoManager.undo());
        this.view.getController().getUndoMI().textProperty().bind(undoRedoManager.undoLabelProperty());
        this.view.getController().getUndoMI().disableProperty().bind(undoRedoManager.canUndoProperty().not());

        this.view.getController().getRedoMI().setOnAction(a->undoRedoManager.redo());
        this.view.getController().getRedoMI().textProperty().bind(undoRedoManager.redoLabelProperty());
        this.view.getController().getRedoMI().disableProperty().bind(undoRedoManager.canRedoProperty().not());
        //__________________________________________________________________monitor property changes and add to manager
        this.view.getController().getMoleculeSizeSlider().valueProperty().addListener((v, o, n) -> undoRedoManager.add(new PropertyCommand<>("atoms radius", (DoubleProperty)v, o, n)));
        this.view.getController().getBondsSizeSlider().valueProperty().addListener((v, o, n) -> undoRedoManager.add(new PropertyCommand<>("bonds size", (DoubleProperty)v, o, n)));
        this.view.getController().getColoringComboBox().valueProperty().addListener((v, o, n) ->
                undoRedoManager.add(new SimpleCommand("coloring", ()-> this.view.getController().getStyleComboBox().setValue(o), ()-> this.view.getController().getStyleComboBox().setValue(n))));
        this.view.getController().getStyleComboBox().valueProperty().addListener((v, o, n) ->
                undoRedoManager.add(new SimpleCommand("drawing style", ()-> this.view.getController().getStyleComboBox().setValue(o), ()-> this.view.getController().getStyleComboBox().setValue(n))));
        this.view.getController().getShowBallsCheckbox().selectedProperty().addListener((v, o, n) -> undoRedoManager.add(new PropertyCommand<>("balls", (BooleanProperty)v, o, n)));
        this.view.getController().getShowBondsCheckbox().selectedProperty().addListener((v, o, n) -> undoRedoManager.add(new PropertyCommand<>("sticks", (BooleanProperty)v, o, n)));
        this.view.getController().getAtomChoiceBox().valueProperty().addListener((v, o, n) -> {
            undoRedoManager.add(new SimpleCommand("atom kind", ()-> this.view.getController().getAtomChoiceBox().setValue(o),()-> this.view.getController().getAtomChoiceBox().setValue(n)));
            //bindings for different atom groups are created to spheres but if checkbox were unselected and a change occurs, it shall be selected again.
            if(n != null && !Objects.equals(n, o)) {
                this.view.getController().getShowBallsCheckbox().setSelected(true);
            }
        });
        //there is no sense in undoing animation or explosion, therefore no listeners for those.
    }

    /**
     * Create listener related to the left side of the window:
     * the list of PDB entry ids, which shall be able to show only sars cov related IDs or all PDB entry IDs,
     * and the search field to filter the list
     */
    private void createIDListListener() {
        //add listener to update currentFileStringProperty with new selection in list
        this.view.getController().getIdListView().getSelectionModel().selectedItemProperty().addListener((v, o, n) -> {
            this.selectionModel.clearSelection();
            this.selectedFileNameProperty.setValue(n);
        });

        //add listener to listen for changes in search field
        this.view.getController().getSearchTextField().textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                List<String> searchedIDs = new ArrayList<>();
                int allIDsSize;
                try {
                    allIDsSize = pdbWebClient.allEntryIDsProperty().size();
                    if (visibleEntryIdListProperty.size() < allIDsSize) {
                        //we want to search in all IDs
                        visibleEntryIdListProperty.set(pdbWebClient.allEntryIDsProperty().get());
                        notAllIDsAreShown.set(false);
                    }
                } catch (IOException e) {
                    this.view.displayErrorMessage("Error requesting PDB IDs", e.getMessage());
                }

                if (newValue.isBlank()) {
                    //all values shall be shown again.
                    searchedIDs = new ArrayList<>(visibleEntryIdListProperty);

                } else {
                    //ignore spaces and if upper or lower case
                    String searchTerm = newValue.toUpperCase().trim();

                    for (int i = 0; i < visibleEntryIdListProperty.getSize(); i++) {
                        if (visibleEntryIdListProperty.get(i).contains(searchTerm)) {
                            searchedIDs.add(visibleEntryIdListProperty.get(i));
                        }
                        //if current ID does not contain search term, check next ID
                    }
                }
                visibleEntryIdListProperty.setAll(searchedIDs);
                notAllIDsAreShown.set(true);
            } else {
                System.err.println("Search Text Fields new value was null, which is invalid and therefore is ignored. ");
            }
        });

        //add listener for selected list view: sars-cov2 or all:
        this.view.getController().getListAllButton().setOnAction(a -> {
            try {
                pdbWebClient.requestAllPDBIDs();
                ListProperty<String> allIDs = new SimpleListProperty<>();
                if(this.currentFileStringProperty.get() != null && !this.currentFileStringProperty.get().isBlank()){
                    allIDs.add(this.currentFileStringProperty.get());               //ensure, if there was a selection, this will still be shown on top.
                }
                allIDs.addAll(pdbWebClient.allEntryIDsProperty().get());
                this.visibleEntryIdListProperty.set(allIDs);
                this.notAllIDsAreShown.set(false);
            } catch (IOException e) {
                this.view.displayErrorMessage("Error loading all PDB IDs", "An error occurred when requesting all PDB IDs from the server. List will not be updated, sorry.");
            }
        });
        this.view.getController().getSarsCovButton().setOnAction(a -> {
            ListProperty<String> sarsCovPlusSelected = new SimpleListProperty<>();
            if(this.currentFileStringProperty.get() != null && !this.currentFileStringProperty.get().isBlank()){
                sarsCovPlusSelected.add(this.currentFileStringProperty.get());               //ensure, if there was a selection, this will still be shown on top.
            }
            sarsCovPlusSelected.addAll(pdbWebClient.sarsCovEntryIDsProperty().get());
            this.visibleEntryIdListProperty.set(sarsCovPlusSelected);
            this.notAllIDsAreShown.set(true);
        });
    }

    /**
     * Create Listener for the three services
     * 1. to read PDB file content,
     * 2. to create molecules from the read file content and
     * 3. to set up the visualization of the created molecules shapes.
     */
    private void createServiceListener() {
        this.selectedFileNameProperty.addListener((v, o, n) -> {
            this.view.setMoleculeInfo("Parsing file with ID " + n);
            this.readFileContentService.restart();
        });

        this.readFileContentService.setOnScheduled(s -> this.currentFileStringProperty.setValue("Reading file..."));
        this.readFileContentService.setOnSucceeded(success -> {
            this.currentFileStringProperty.setValue(readFileContentService.getValue());
            createMoleculeService.restart();
            this.view.setMoleculeInfo("Creating Molecules from file...");
            readFileContentService.reset();//otherwise service never finishes...
        });

        this.createMoleculeService.setOnSucceeded(s -> {
            model = createMoleculeService.getValue();
            setupMoleculeVisualizationService.restart();
            view.getController().getMoleculeNameLabel().setText(model.getName());
            createMoleculeService.reset();//otherwise service never finishes...
        });

        this.setupMoleculeVisualizationService.setOnCancelled(s -> this.view.setMoleculeInfo("Setting up molecules view was cancelled:" + setupMoleculeVisualizationService.getMessage()));
        this.setupMoleculeVisualizationService.setOnSucceeded(s->{
            finishSettingUp();
            setupMoleculeVisualizationService.reset();//otherwise service never finishes...
        });

        this.readFileContentService.exceptionProperty().addListener((v, o, n) -> {
            if (n != null) {
                view.displayErrorMessage("Error reading file", n.getMessage());
            }
        });
        this.createMoleculeService.exceptionProperty().addListener((v, o, n) -> {
            if (n != null) {//there will be exceptions sometimes, if this is not checked, even though it should never be null
                view.displayErrorMessage("Error creating molecule", n.getMessage());
            }
        });
        this.setupMoleculeVisualizationService.exceptionProperty().addListener((v, o, n) -> {
            if (n != null) {//there will be exceptions sometimes, if this is not checked, even though it should never be null
                view.displayErrorMessage("Error setting up molecules view", n.getMessage());
            }
        });
    }

    /**
     * Propagate the results of setupMoleculeVisualizationService to WindowPresenter and draw charts
     */
    private void finishSettingUp() {
        view.setMoleculeInfo("Setting up Molecules view...");
        atom2sphere = setupMoleculeVisualizationService.getValue();
        view.setMoleculeInfo(model.getPolymers().size() + " Polymers with " + model.getNumberOfAtoms() +" atoms in total are shown.");
        setupCharts();
    }

    /**
     * Generates the text to report on the current selection and returns it.
     * The resulting text will contain all currently selected atoms together with their monomer and polymer names as long as the resulting string has a length smaller than or equal to 100.
     * Otherwise, the text will be shortened from the right.
     * When several Atoms of the same monomer in the same Polymer are selected, those will be reported together.
     * @return the resulting text of selected atoms.
     */
    private String generateSelectionInfoText() {
        Set<AtomI> selectedAtoms = this.selectionModel.getSelectedItems();
        String starting = "Selected atoms: ";
        StringBuilder selectionText = new StringBuilder();

        if(selectedAtoms.size()>0) {
            for (AtomI atom : selectedAtoms) {
                List<Polymer> polymers = this.model.getPolymers();
                for (Polymer p : polymers) {
                    List<Monomer> monomers = p.getMonomers();
                    for (Monomer m : monomers) {
                        if (m.getAtoms().contains(atom)) {
                            // found matching monomer
                            String location = " in " + m.getType().name() + " (Polymer " + p.getUnitID() + ") ";
                            if(selectionText.toString().contains(location)){
                                //atom from same monomer is already selected --> combine report on those:
                                String[] selectTextParts = selectionText.toString().split(location, 0); //should give exactly two Strings
                                if(selectTextParts.length == 2) {
                                    selectionText = new StringBuilder(selectTextParts[0] + ", " + atom.getName().get() + location + "; " + selectTextParts[1]); //insert newly selected atom Name
                                    break;
                                }
                                else{
                                    //only one other atom was selected by now --> location is at end, so size is only 1
                                    selectionText = new StringBuilder(atom.getName().get() + ", " + selectTextParts[0]);
                                    break;
                                }
                            }
                            else{
                                if(!selectionText.toString().isBlank()){
                                    selectionText.append("; ");
                                }
                                selectionText.append(atom.getName().get()).append(location);
                                break;
                            }
                        }
                    }
                }
            }
            while(selectionText.length() > 100){
                //remove last reporting, which ends with closing bracket after polymer unit ID
                selectionText = new StringBuilder(selectionText.substring(0, selectionText.lastIndexOf("; ") + 1));
            }
            selectionText.insert(0, starting);
        }
        else{
            return "All atoms are unselected.";
        }
        return selectionText.toString();
    }

    /**
     * Create the Charts according to the currently displayed Molecules model object.
     */
    private void setupCharts() {
        this.view.getController().getChartsTitle().setText(this.model.getName());

        Set<String> monomerNames = this.model.getMonomerNames();
        boolean hasAminos = false, hasNucleotides = false;
        for(String monoName : monomerNames){
            if(this.view.getController() != null){
                List<AtomSphere> monosSpheres = this.view.getSpheresReferences().get(monoName);
                if(monosSpheres != null){
                    int numberOfAtoms = monosSpheres.size();
                    if(monoName.length() == 3) {
                        this.view.getController().getAminoAcidsPieChart().getData().add(new PieChart.Data(monoName, numberOfAtoms));
                        hasAminos = true;
                    }else{
                        this.view.getController().getNucleotidesPieChart().getData().add(new PieChart.Data(monoName, numberOfAtoms));
                        hasNucleotides = true;
                    }
                }
            }
        }
        this.view.getController().getAminoAcidsPieChart().setVisible(hasAminos);
        this.view.getController().getNucleotidesPieChart().setVisible(hasNucleotides);

        this.view.getController().getAminoAcidsPieChart().setLegendVisible(false);
        this.view.getController().getNucleotidesPieChart().setLegendVisible(false);

        HashMap<String, Integer> atomNumbersTotal = new HashMap<>();

        Set<String> chainNames = this.model.getChainNames();
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Atom per Chains");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Amount of Atoms");

        for(String chain : chainNames){
            XYChart.Series chainAtomsSeries = new XYChart.Series<String, Number>();
            Map<String, Integer> atomsPerChain = this.model.getAtomNumbersOfChain(chain);
            for(String atomName : atomsPerChain.keySet()){
                int atomNumberInChain = atomsPerChain.get(atomName);
                chainAtomsSeries.getData().add(new XYChart.Data<>(atomName, atomNumberInChain));

                //collect data for next chart:
                if(atomNumbersTotal.containsKey(atomName)){
                    atomNumbersTotal.put(atomName, atomNumbersTotal.get(atomName) + atomNumberInChain);
                }
                else{
                    atomNumbersTotal.put(atomName, atomNumberInChain);
                }
            }
            this.view.getController().getAtomsPerChainStructBarChart().getData().add(chainAtomsSeries);
        }
        this.view.getController().getAtomsPerChainStructBarChart().legendVisibleProperty().set(false);


        CategoryAxis horAxis = new CategoryAxis();
        horAxis.setLabel("Atom Names");
        NumberAxis vertAxis = new NumberAxis();
        vertAxis.setLabel("Amount of Atoms");

        for(String chain : chainNames){
            XYChart.Series totalAtomsSeries = new XYChart.Series<String, Number>();
            for(String atomName : atomNumbersTotal.keySet()){
                totalAtomsSeries.getData().add(new XYChart.Data<>(atomName, atomNumbersTotal.get(atomName)));
            }
            this.view.getController().getAtomsBarChart().getData().add(totalAtomsSeries);
        }
        this.view.getController().getAtomsBarChart().legendVisibleProperty().set(false);
    }

    /**
     * Create a file chooser, which allows to open files of the specified type only.
     * @param fileExtension - which shall be allowed to be chosen by the file chooser, if null or empty all file extensions will be allowed.
     * @return the file which was chosen by the user using the file Chooser.
     */
    public File chooseFile(String fileExtension){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(FileParser.DEFAULT_STORING_DIRECTORY));
        if(fileExtension != null && !fileExtension.isBlank()) {
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDB files", "*" + fileExtension));
            fileChooser.setTitle(String.format("Open a file in %s format", fileExtension));
        }
        else{
            fileChooser.setTitle("Open a file specifying the structure");
        }
        return fileChooser.showOpenDialog(this.view.getRoot().getScene().getWindow());
    }

}

