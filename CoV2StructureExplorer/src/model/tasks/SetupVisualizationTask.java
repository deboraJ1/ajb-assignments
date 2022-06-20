package model.tasks;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.MeshView;
import javafx.util.Pair;
import model.MoleculeAnalyzer;
import model.atoms.AtomI;
import model.atoms.CarbonPosition;
import model.molecules.Molecules;
import model.molecules.Monomer;
import model.molecules.Polymer;
import model.molecules.Structure;
import org.apache.commons.lang.NullArgumentException;
import selection.SelectionModel;
import view.AtomSphere;
import view.Window;
import view.helper.Shape3DUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

/**
 * This class is a Task which sets up a visualization of the molecules class.
 * Each atom in the Molecules will be represented by a sphere and each bond between atoms will be represented by a cylinder in green material color.
 * Spheres of a atom will by default have the color which is usually used to represent a specific atom.
 * Selection of spheres will be initialized. This class will create two hash maps:
 * One where each given atom from the Molecules class will be assoziated with the according sphere that was created
 * Another one, where for each Structuring group (like Chain, Sheet, Helix, Monomer, ...) the according spheres are listed.
 * The first one will be the return value of this task, the second one will be set in the view.
 */
public class SetupVisualizationTask extends Task<HashMap<AtomI, AtomSphere>> {

    private Molecules molecules;

    private Window view;
    private PhongMaterial cylinderMaterial;
    private Group moleculeGroup;
    private Group polymersRibbonMeshes;

    private SelectionModel selectionModel;

    private HashMap<AtomI, AtomSphere> atom2sphere;
    private HashMap<String, List<Cylinder>> polymersCylinder;
    private HashMap<String, List<AtomSphere>> shapeReferences;

    /**
     * Constructor of the Task to set up the visualization of molecules
     * @param molecule the molecules to be visualized
     * @param window the window in which to be visualized
     * @param selectionModel the selection model, which will handle spheres selection.
     */
    public SetupVisualizationTask(Molecules molecule, Window window, SelectionModel selectionModel){
        scheduled();
        if(molecule != null){
            this.molecules = molecule;
        }
        else {
            //cancel task if not specified.
            this.setException(new NullArgumentException("Molecules visualization can not be set up from a molecules object which is null."));
            cancel(true);
        }
        if(window != null){
            this.view = window;
        }
        else {
            //cancel task if not specified.
            this.setException(new NullArgumentException("Molecules visualization can not be set up with a view which is null."));
            cancel(true);
        }
        if(selectionModel != null){
            this.selectionModel = selectionModel;
        }
        else {
            //cancel task if selection model not specified.
            this.setException(new NullArgumentException("Molecules visualization can not be set up with a selection model which is null."));
            cancel(true);
        }
        //shall be always the same and therefore is set only once.
        cylinderMaterial = new PhongMaterial();
        cylinderMaterial.setDiffuseColor(Color.GREEN.darker());
        cylinderMaterial.setSpecularColor(Color.GREEN.brighter());

        moleculeGroup = new Group();
        polymersRibbonMeshes = new Group();

        this.shapeReferences = new HashMap<>();
        this.atom2sphere = new HashMap<>();
        this.polymersCylinder = new HashMap<>();
        running();
    }


    /**
     * Set up the visualization of the molecules provided with model.
     * Each atom within this molecule will be displayed in its color and diameter.
     * Selection will be prepared for each atom and references are created to find quickly the according monomer, structure and polymer of an sphere as well as the atom it is representing.
     */
    @Override
    protected HashMap<AtomI, AtomSphere> call() {
        updateProgress(0, 100);                 //progress is starting
        try {
            List<Polymer> polymers = molecules.getPolymers();
            Set<String> containedStructureGroups = MoleculeAnalyzer.getAllStructureNamesFromMolecule(molecules);
            for (String structGroup : containedStructureGroups) {
                this.shapeReferences.put(structGroup, new ArrayList<>());
            }
            updateProgressValue(20);

            for (int i = 0; i < polymers.size(); i++) {
                String polyType = polymers.get(i).getUnitID().trim().toUpperCase();
                List<Structure> structures = polymers.get(i).getStructures();

                for (int j = 0; j < structures.size(); j++) {
                    List<Monomer> monomers = structures.get(j).getMonomers();
                    String structID = structures.get(j).getId().trim().toUpperCase();
                    String structType = structures.get(j).getStructureType().name().trim().toUpperCase();

                    for (int k = 0; k < monomers.size(); k++) {
                        Monomer m = monomers.get(k);
                        //Balls and Sticks visualization
                        List<AtomSphere> monomersSpheres = createMonomerSpheres(i, j, k, m.getAtoms(), this.selectionModel);

                        //keep references for this monomer type
                        String monoType = m.getType().name().trim().toUpperCase();
                        List<AtomSphere> monoRef = this.shapeReferences.get(monoType);
                        if (monoRef == null) {
                            monoRef = new ArrayList<>();
                        }
                        monoRef.addAll(monomersSpheres); //should be initialized with empty list --> no check needed
                        this.shapeReferences.put(monoType, monoRef);

                        //keep reference for this structure
                        List<AtomSphere> singleStructureRef = this.shapeReferences.get(structID);
                        if (singleStructureRef == null) {     //first monomer of this structure; was not yet initialized
                            singleStructureRef = new ArrayList<>();
                        }
                        singleStructureRef.addAll(monomersSpheres);
                        this.shapeReferences.put(structID, singleStructureRef);

                        //keep reference for this structure type
                        List<AtomSphere> structureRef = this.shapeReferences.get(structType);
                        if (structureRef == null) {
                            structureRef = new ArrayList<>();
                        }
                        structureRef.addAll(monomersSpheres); //should be initialized with empty list --> no check needed
                        this.shapeReferences.put(structType, structureRef);

                        //keep reference for this polymer
                        List<AtomSphere> polyRef = this.shapeReferences.get(polyType);
                        if (polyRef == null) {
                            polyRef = new ArrayList<>();
                        }
                        polyRef.addAll(monomersSpheres); //should be initialized with empty list --> no check needed
                        this.shapeReferences.put(polyType, polyRef);

                        List<Cylinder> polymersCylinder = createMonomersBonds(i, j, k, m.getBonds());
                        List<Cylinder> polysCylinder = this.polymersCylinder.get(polyType);
                        if (polysCylinder == null) {
                            polysCylinder = new ArrayList<>();
                        }
                        polysCylinder.addAll(polymersCylinder);
                        this.polymersCylinder.put(polyType, polymersCylinder);

                    }
                }

                //Ribbon visualization
                List<Monomer> allMonosOfPoly = polymers.get(i).getMonomers();
                Group polysMeshes = new Group();
                for(int k = 1; k<allMonosOfPoly.size(); k++){
                        Monomer prevMonomer = allMonosOfPoly.get(k-1);
                        Monomer currMonomer = allMonosOfPoly.get(k);
                        MeshView ribbonMesh = createMesh(prevMonomer, currMonomer);
                        if(ribbonMesh != null){
                            polysMeshes.getChildren().add(ribbonMesh);
                        }
                }
                if(polysMeshes.getChildren().size() > 0) {
                    polymersRibbonMeshes.getChildren().add(polysMeshes);
                }
                if (i == Math.floorDiv(polymers.size(), 2)) {
                    updateProgressValue(50);
                }
            }
            Platform.runLater(() -> {
                this.view.setMoleculeGroup(this.moleculeGroup);
                this.view.setPolymersRibbonMeshGroup(this.polymersRibbonMeshes);
                this.view.resetMoleculeView();
                this.view.setSpheresReferences(this.shapeReferences);
                this.view.setPolymersCylinder(this.polymersCylinder);
            });
            updateProgress(100, 100);

            return atom2sphere;
        }
        catch (Exception e){
            this.setException(e);
            cancel();
        }
        return null;
    }

    /**
     * Create all atoms of one monomer. Atoms will be represented by AtomSpheres.
     * @param polymerNumber the number of the polymer, needs to be greater than or equal to 0
     * @param structureNumber number of the structure within all structures of polymer number polymerNumber, needs to be greater than or equal to 0
     * @param monomerNumber number of the monomer within all monomers of structure number structureNumber in polymer of number polymerNumber, needs to be greater than or equal to 0
     * @param monomersAtoms a list containing all atoms, which are representing this monomer.
     * @param selectionModel the model handling selection events
     * @return the list of AtomSpheres which have been created from the list of atoms.
     */
    public List<AtomSphere> createMonomerSpheres(int polymerNumber, int structureNumber, int monomerNumber, List<AtomI> monomersAtoms, SelectionModel selectionModel){
        if(polymerNumber >= 0 && structureNumber >= 0 && monomerNumber >= 0 && monomersAtoms != null && selectionModel != null) {
            List<AtomSphere> monomersSpheres = new ArrayList<>();

            for (AtomI atom : monomersAtoms){
                AtomSphere sphere = createAtomSphere(atom, selectionModel);
                monomersSpheres.add(sphere);
                this.atom2sphere.put(atom, sphere);
            }
            Group monomerAtomsGroup = new Group();
            monomerAtomsGroup.getChildren().addAll(monomersSpheres);
            monomerAtomsGroup.visibleProperty().bind(this.view.getController().getShowBallsCheckbox().selectedProperty());

            // add spheres to groups
            addShapeToMonomerGroup(polymerNumber, structureNumber, monomerNumber, monomerAtomsGroup);
            return monomersSpheres;
        }
        return null;
    }

    /**
     * Create all connections/bonds between atoms of one monomer. Bonds will be represented by Cylinders.
     * @param polymerNumber the number of the polymer, needs to be greater than or equal to 0
     * @param structureNumber number of the structure within all structures of polymer number polymerNumber, needs to be greater than or equal to 0
     * @param monomerNumber number of the monomer within all monomers of structure number structureNumber in polymer of number polymerNumber, needs to be greater than or equal to 0
     * @param monomersBonds a list containing pairs of centers of atoms, which are to be connected.
     * @return the list of Cylinders which have been created from the list of Point-pairs.
     */
    public List<Cylinder> createMonomersBonds(int polymerNumber, int structureNumber, int monomerNumber, List<Pair<Point3D, Point3D>> monomersBonds){
        if(polymerNumber >= 0 && structureNumber >= 0 && monomerNumber >= 0  && monomersBonds != null) {
            List<Cylinder> monomerCylinders = new ArrayList<>();

            Group monomerBondGroup = new Group();
            for(Pair<Point3D, Point3D> bond : monomersBonds) {
                //get needed Points and values:
                Point3D centerA = bond.getKey();
                Point3D centerB = bond.getValue();

                Point3D midPoint = centerA.midpoint(centerB);               //midpoint lying on axis of cylinder, middle between A and B
                Point3D direction = centerB.subtract(centerA);
                Point3D perpendicularAxis = Shape3DUtils.YAXIS.crossProduct(direction);  //perpendicular to the direction of the line between A and B
                double angle = Shape3DUtils.YAXIS.angle(direction);

                Cylinder bondCylinder = createBondCylinder(perpendicularAxis, midPoint, angle);
                //fit bond so that it ends at atom balls
                bondCylinder.setScaleY(centerA.distance(centerB) / bondCylinder.getHeight());
                bondCylinder.radiusProperty().bind(this.view.getController().getBondsSizeSlider().valueProperty().multiply(bondCylinder.getRadius() * 0.05));
                monomerCylinders.add(bondCylinder);
                monomerBondGroup.getChildren().add(bondCylinder);
            }
            monomerBondGroup.visibleProperty().bind(this.view.getController().getShowBondsCheckbox().selectedProperty());

            // add bonds to groups
            addShapeToMonomerGroup(polymerNumber, structureNumber, monomerNumber, monomerBondGroup);
            return monomerCylinders;
        }
        return null;
    }

    /**
     * Create the according sphere which will represent the given atom and prepare selection of that atoms sphere.
     * @param atom the atom to be represented by a sphere
     * @param selectionModel handling the selection of the created sphere
     * @return a sphere representing the given atom.
     */
    private AtomSphere createAtomSphere(AtomI atom, SelectionModel selectionModel) {
        AtomSphere sphere = null;
        if(atom != null && selectionModel != null){
            sphere = new AtomSphere(atom.getName(), atom.getLetter(), atom.getLocation(), atom.getRadiusPM(), atom.getColor(), atom.getTempFactor());
            sphere.radiusProperty().bind(this.view.getController().getMoleculeSizeSlider().valueProperty().multiply(sphere.getRadius() * 0.01));
            sphere.visibleProperty().bind(this.view.getController().getAtomChoiceBox().selectionModelProperty().get().selectedItemProperty().isEqualTo("All").or(this.view.getController().getAtomChoiceBox().selectionModelProperty().get().selectedItemProperty().isEqualTo(atom.getName())));
            sphere.setOnMouseClicked(e -> {
                if(!e.isShiftDown()){
                    selectionModel.clearSelection();
                }
                selectionModel.select(atom);
            });
        }
        return sphere;
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
            Cylinder bondCylinder = new Cylinder(0.1, 1);
            bondCylinder.setRotationAxis(perpendicularAxis);
            bondCylinder.setRotate(angle);

            //translate to middle between centerA and centerB
            bondCylinder.setTranslateX(midPoint.getX());
            bondCylinder.setTranslateY(midPoint.getY());
            bondCylinder.setTranslateZ(midPoint.getZ());

            bondCylinder.setCullFace(CullFace.BACK);
            bondCylinder.setDrawMode(DrawMode.FILL);
            bondCylinder.setMaterial(cylinderMaterial);
            return bondCylinder;
        }
        else{
            System.err.println("Tried to create bond between points which were null.");
            return null;
        }
    }

    /**
     * Create a Mesh to show ribbon view. The Mesh is to be created between Carbon atoms of monomer 1 and monomer 2.
     * @param monomer1 monomer containing guiding and twisting Carbon, which are defining one side of the mesh to be created.
     *                 shall not be null
     * @param monomer2 second monomer containing guiding and twisting Carbon, which are defining one side of the mesh to be created.
     *                 shall not be null
     *                 Bot Monomer can additionally define an opposite Carbon but this can be computed if not contained.
     *                 Both monomer need to be of the same type so that a mesh can be created, so either both amino acids or both nucleotides.
     *                 If any of the two monomers has no guiding or no twisting monomer, than this monomer will not be considered.
     * @return the MeshView between monomer1 and monomer2 if it could be created.
     */
    private MeshView createMesh(Monomer monomer1, Monomer monomer2){
        if(monomer1 != null && monomer2 != null){
            String type1 = monomer1.getType().name();
            String type2 = monomer2.getType().name();
            if((type1.length() == 3 && type2.length() == 3) || (type1.length() < 3 && type2.length() < 3)){
                //only makes sense if the monomers have the same type.
                Point3D guidingPos1 = monomer1.getRibbonPosition(CarbonPosition.GUIDE);
                Point3D twistingPos1 = monomer1.getRibbonPosition(CarbonPosition.TWIST);

                if(guidingPos1 != null && twistingPos1 != null){ //otherwise we dont need to continue computing.
                    Point3D oppositePos1 = monomer1.getRibbonPosition(CarbonPosition.OPPOSITE);
                    if(oppositePos1 == null){
                        try{
                            oppositePos1 = Shape3DUtils.computeOpposite(guidingPos1, twistingPos1);
                        }
                        catch (Exception e){
                            //one or both positions might be null
                            setException(e);
                            cancel();
                        }
                    }

                    Point3D guidingPos2 = monomer2.getRibbonPosition(CarbonPosition.GUIDE);
                    Point3D twistingPos2 = monomer2.getRibbonPosition(CarbonPosition.TWIST);

                    if(guidingPos2 != null && twistingPos2 != null) { //otherwise we dont need to continue computing.
                        Point3D oppositePos2 = monomer2.getRibbonPosition(CarbonPosition.OPPOSITE);
                        if (oppositePos2 == null) {
                            try {
                                oppositePos2 = Shape3DUtils.computeOpposite(guidingPos2, twistingPos2);
                            } catch (Exception e) {
                                //one or both positions might be null
                                setException(e);
                                cancel();
                            }
                        }
                        return Shape3DUtils.createRibbonPart(guidingPos1, twistingPos1, oppositePos1, guidingPos2, twistingPos2, oppositePos2);
                    }

                }
                System.out.println("Monomer " + monomer1.getType() + " (" + monomer1.getSequenceNumber() + ") has no guiding or no twisting Carbon atom.");
            }
            else{
                System.err.println("Monomers are not of the same type. Mesh can not be calculated.");
            }
        }
        else{
            setException(new NullArgumentException("Mesh between Monomers where at least one Monomer is null, can not be calculated."));
            cancel();
        }
        return null;
    }

    /**
     * Add the given group of shapes to the Group of monomer with monomerNumber in structure with structureNumber of polymer with polymerNumber
     * @param polymerNumber index of the polymers group which is searched, and where it can be found in the molecule group.
     * @param structureNumber index of the structure group which is searched, and where it can be found in the polymer group.
     * @param monomerNumber index of the monomer group which is searched, and where it can be found in the structure group.
     * @param shapes group of shapes to be added.
     */
    private void addShapeToMonomerGroup(int polymerNumber, int structureNumber, int monomerNumber, Group shapes){
        if(polymerNumber >= 0 && structureNumber >= 0 && monomerNumber >= 0) {
            //check if according groups already exist
            while (moleculeGroup.getChildren().size() <= polymerNumber) {
                moleculeGroup.getChildren().add(new Group());
            }

            while (((Group) moleculeGroup.getChildren().get(polymerNumber)).getChildren().size() <= structureNumber) {
                ((Group) moleculeGroup.getChildren().get(polymerNumber)).getChildren().add(new Group());
            }

            while (((Group) ((Group) moleculeGroup.getChildren().get(polymerNumber)).getChildren().get(structureNumber)).getChildren().size() <= monomerNumber) {
                ((Group) ((Group) moleculeGroup.getChildren().get(polymerNumber)).getChildren().get(structureNumber)).getChildren().add(new Group());
            }

            ((Group) ((Group) ((Group) moleculeGroup.getChildren().get(polymerNumber)).getChildren().get(structureNumber)).getChildren().get(monomerNumber)).getChildren().add(shapes);
        }
    }

    /**
     * update the progress value of the current task to the given value as current progress value of a maximum of 100.
     * @param updateValue the value to which the progress will be set from 100, needs to be and greater than or equal to 0 and smaller than or equal to 100
     */
    public void updateProgressValue(double updateValue){
        if(updateValue >= 0 && updateValue <= 100){
            updateProgress(updateValue, 100);
        }
        //otherwise dont do anything
    }
}
