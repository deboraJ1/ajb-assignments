
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;

import javafx.util.Pair;
import model.Atom;
import model.Molecule;
import model.WaterMolecule;

import java.io.IOException;
import java.util.List;

/**
 * The window presenter
 * This is where we communicate between the view and the model
 * Daniel Huson, 4.2021
 * Debora Jutz, 03.06.2021
 */
public class WindowPresenter {

    private static int radiiStepSize = 5;                   // step size for increasing and decreasing atom balls per click on buttons ++ and --
    private final Point3D YAXIS = new Point3D(0, 100, 0);
    private PhongMaterial cylinderMaterial;

    private Group molecule, atomBalls, bondCylinders;                 // contain 3D objects
    private Property<Transform> figureTransformProperty = new SimpleObjectProperty<>(new Rotate());

    private SubScene cameraScene;
    private Camera camera;
    private Window view;
    private Molecule model;

    /**
     * Constructor of WindowPresenter creating a new instance of WindowPresenter to manage communication between View and Model.
     * @param stage
     * @param view - to communicate with; not null. If null, a default instance of Window will be created.
     * @param model - to communicate with; not null. If null, a default instance of Molecule will be created.
     */
    public WindowPresenter(Stage stage, Window view, Molecule model) {
        if(view != null){
            this.view = view;
        }
        else{
            try{
                this.view = new Window();
                System.err.println("Window can not be null! A default Window was created.");
            }
            catch (IOException e){
                System.err.println("Sorry an error occurred when creating a default Window object.");
            }
        }
        if(model != null){
            this.model = model;
        }
        else {
            this.model = new WaterMolecule();
            System.err.println("Model can not be null! A default Model was created.");

        }
        //shall be always the same and therefore is set only once.
        cylinderMaterial = new PhongMaterial();
        cylinderMaterial.setDiffuseColor(Color.BLACK.darker());
        cylinderMaterial.setSpecularColor(Color.BLACK.brighter());

        molecule = new Group();
        setupMoleculeVisualization();
        molecule.getChildren().addAll(atomBalls, bondCylinders);

        createCamera();

        //create Mouse interaction, bindings, listener
        MouseInteraction.installRotate(this.view.getController().getCenterPane(), figureTransformProperty);
        addBindings();
        addListener();
    }

    /**
     * create a camera for 3D objects and its own subscene
     */
    private void createCamera() {
        this.camera = new PerspectiveCamera(true);
        this.camera.setFarClip(100000);
        this.camera.setNearClip(0.01);
        //to see molecule in middle of window
        this.camera.setTranslateZ(-1000); //to ensure all objects are seen

        this.cameraScene = new SubScene(molecule, 800, 800, true, SceneAntialiasing.BALANCED);
        this.cameraScene.setCamera(camera);
        this.view.getController().getCenterPane().getChildren().add(cameraScene);
    }

    /**
     * Set up the visualization of the molecules provided with model.
     * Each atom within this molecule will be displayed in its color and diameter.
     */
    private void setupMoleculeVisualization() {
        atomBalls = new Group(); //is always created newly because different models should not be mixed (therefore no check for != null)
        bondCylinders = new Group();

        //create atoms of molecule
        int atoms = model.getNumberOfAtoms();
        for(int i = 0; i<atoms; i++){
            Atom atom = model.getAtom(i);
            Sphere atomSphere = new Sphere(atom.getRadiusPM());

            //create material of sphere
            PhongMaterial atomMaterial = new PhongMaterial();
            atomMaterial.setDiffuseColor(atom.getColor().darker());
            atomMaterial.setSpecularPower(3.4);
            atomMaterial.setSpecularColor(atom.getColor().brighter());
            atomSphere.setMaterial(atomMaterial);

            //set position of sphere
            Point3D atomPosition = model.getLocation(i);
            atomSphere.setTranslateX(atomPosition.getX());
            atomSphere.setTranslateY(atomPosition.getY());
            atomSphere.setTranslateZ(atomPosition.getZ());

            atomBalls.getChildren().add(atomSphere);
        }

        //create sticks of molecule
        List<Pair<Integer,Integer>> bonds = model.bonds();

        for (int j = 0; j<bonds.size(); j++) {
            //get needed Points and values:
            Pair<Integer, Integer> bondPair = bonds.get(j);

            int atomAIndex = bondPair.getKey();
            int atomBIndex = bondPair.getValue();
            Point3D centerA = model.getLocation(atomAIndex);
            Point3D centerB = model.getLocation(atomBIndex);

            Point3D midPoint = centerA.midpoint(centerB);               //midpoint lying on axis of cylinder, middle between A and B
            Point3D direction = centerB.subtract(centerA);
            Point3D perpendicularAxis = YAXIS.crossProduct(direction);  //perpendicular to the direction of the line between A and B
            double angle = YAXIS.angle(direction);

            Cylinder bondCylinder = createBondCylinder(perpendicularAxis, midPoint, angle);
            //fit bond so that it ends at atom balls
            bondCylinder.setScaleY(centerA.distance(centerB) / bondCylinder.getHeight());

            bondCylinders.getChildren().add(bondCylinder);
        }
    }

    /**
     * Create a cylinder to represent the bond between two points. Midpoint is a Point lying directly in the middle of the two points the cylinder will connect.
     * @param perpendicularAxis - axis perpendicular to the cylinders orientation
     * @param midPoint - point in the middle between A and B (points which are connected by resulting Cylinder)
     * @param angle - angle between Y-axis and the direction of the cylinder
     * @return Cylinderobject with default material. midPoint will ly on the Cylinderobject and the orientation will be differ by the given angle from Y-Axis.
     *          PerpendicularAxis will be perpendicular to the orientation of the Cylinderobject
     */
    private Cylinder createBondCylinder(Point3D perpendicularAxis, Point3D midPoint, double angle){
        if(perpendicularAxis != null && midPoint != null) {
            Cylinder bondCylinder = new Cylinder(10, 100);
            bondCylinder.setRotationAxis(perpendicularAxis);
            bondCylinder.setRotate(angle);

            //translate to middle between centerA and centerB
            bondCylinder.setTranslateX(midPoint.getX());
            bondCylinder.setTranslateY(midPoint.getY());
            bondCylinder.setTranslateZ(midPoint.getZ());

            bondCylinder.setMaterial(cylinderMaterial);
            return bondCylinder;
        }
        else{
            this.view.displayErrorMessage("Error creating bonds", "Bonds between points which are null can not be created.");
            return null;
        }
    }

    /**
     * Add bindings to
     *  disable / enable control elements of view depending on state of other properties
     *  as well as keeping scene sizes consistent
     */
    private void addBindings(){
        //enable larger and smaller button only when balls are visible
        this.view.getController().getLargerButton().disableProperty().bind(this.view.getController().getBallsCheckButton().selectedProperty().not());
        this.view.getController().getSmallerButton().disableProperty().bind(this.view.getController().getBallsCheckButton().selectedProperty().not());

        //bind cameraScene size to top scene size
        this.cameraScene.widthProperty().bind(this.view.getController().getCenterPane().widthProperty());
        this.cameraScene.heightProperty().bind(this.view.getController().getCenterPane().heightProperty());
    }

    /**
     * Add listener to control elements of the view.
     */
    private void addListener(){
        this.view.getController().getCloseMenuItem().setOnAction(e-> System.exit(0));

        // listener for mouse scrolling
        this.view.getController().getCenterPane().setOnScroll(s -> camera.setTranslateZ(camera.getTranslateZ() + s.getDeltaY()));
        //listen for rotation
        this.figureTransformProperty.addListener((v, o, n) -> {
            //if balls are not visible they should not move on drags
            if(atomBalls.isVisible()) {
                atomBalls.getTransforms().setAll(n);
            }
            if(bondCylinders.isVisible()){
                bondCylinders.getTransforms().setAll(n);
            }
        });

        //listener for check boxes to make balls and bonds (in)visible
        this.view.getController().getBallsCheckButton().setOnAction(a -> {this.atomBalls.setVisible(this.view.getController().getBallsCheckButton().isSelected());});
        this.view.getController().getSticksCheckButton().setOnAction(a -> {this.bondCylinders.setVisible(this.view.getController().getSticksCheckButton().isSelected());});

        //listener for changing size of balls
        this.view.getController().getSmallerButton().setOnAction(a -> changeMoleculeSizeBy(-radiiStepSize));
        this.view.getController().getLargerButton().setOnAction(a -> changeMoleculeSizeBy(radiiStepSize));
    }

    /**
     * Change the radii of all atoms and their bonds within current molecule model by value of change.
     * @param change - value how much to change the radii of all atom balls. Can be smaller or greater than zero.
     *               Value smaller than zero decreases the radii, value greater than zero increases the radii.
     *               Value equal to zero has no effect.
     *               If the radius of any atom would be smaller or equal than zero after adding change, this change of radius is not performed.
     *               Therefore, the radii of the atoms are not guaranteed to be consistent.
     *               Radii of bonds are changed by 1/change, so a changing step for radii of bonds is one third of the changing step for radii of the atoms.
     */
    private void changeMoleculeSizeBy(int change){
        if(change != 0){
            for (Node atom : atomBalls.getChildren()) {
                double currentRadius = ((Sphere) atom).getRadius();
                if((currentRadius + change) > 0){
                    ((Sphere)atom).setRadius(currentRadius + change);
                }
                else {
                    //some atoms would become invisible therefore just dont change those anymore.
                    this.view.displayWarning("Changing Radii", "Proportions of atoms can not be guaranteed anymore. Because some atoms would be invisible.");
                }
            }

            //also change size of bonds (was not part of assignment but looks better to me)
            change = change/3; //change is too much for bonds --> looks better if changing steps are 1/3 of atom change
            for(Node bond : bondCylinders.getChildren()){
                double currentRadius = ((Cylinder) bond).getRadius();
                if((currentRadius + change) > 0){
                    ((Cylinder)bond).setRadius(currentRadius + change);
                }
                else {
                    //some bonds would become invisible therefore just dont change those anymore.
                    this.view.displayWarning("Changing Radii", "Proportions of bonds can not be guaranteed anymore. Because some atoms would be invisible.");
                }
            }
        }
        //else no change anyways
    }

}
