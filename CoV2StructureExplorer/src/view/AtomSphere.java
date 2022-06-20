package view;

import javafx.beans.property.*;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;

public class AtomSphere extends Sphere {
    private StringProperty name;
    private StringProperty letter;
    private DoubleProperty radius = new SimpleDoubleProperty();
    private ObjectProperty<Point3D> location = new SimpleObjectProperty<>();
    public final Color originalAtomColor;
    private double tempFactor;

    /**
     * Create a sphere to represent the atom, which is defined by the given parameters.
     * Material of the atom will be according to the specification within the given atom
     * @param name name of the atom which is represented by this sphere
     * @param letter letter of the atom which is represented by this sphere
     * @param location location of the atom which is represented by this sphere
     * @param radius radius of the atom to be represented by this sphere, the sphere will have a radius which is divided by 50  to make the molecule not too big.
     * @param color color of the atom which is represented by this sphere
     * @param tempFactor temperature factor of the atom which is represented by this sphere
     */
    public AtomSphere(StringProperty name, StringProperty letter, Property<Point3D> location, IntegerProperty radius, Color color, double tempFactor) {
        super(radius.get()/50, 15);
        if(name != null){
            this.name = new SimpleStringProperty(name.get());
            
            if ("Carbon".equalsIgnoreCase(name.get())) {
                this.originalAtomColor = Color.GRAY;
            } else if ("Hydrogen".equalsIgnoreCase(name.get())) {
                this.originalAtomColor = Color.WHITE;
            } else if ("Oxygen".equalsIgnoreCase(name.get())) {
                this.originalAtomColor = Color.RED;
            } else if ("Nitrogen".equalsIgnoreCase(name.get())) {
                this.originalAtomColor = Color.BLUE;
            } else if ("Phosphor".equalsIgnoreCase(name.get())) {
                this.originalAtomColor = Color.ORANGE;
            } else if ("Sulfur".equalsIgnoreCase(name.get())) {
                this.originalAtomColor = Color.YELLOW;
            } else {
                this.originalAtomColor = Color.GREEN;
            }
        }
        else{
            this.originalAtomColor = Color.GREEN;
        }
        if(letter != null){
            this.letter = new SimpleStringProperty(letter.get());
        }
        if(location != null){
            Point3D atomPos = location.getValue();
            this.setTranslateX(atomPos.getX());
            this.setTranslateY(atomPos.getY());
            this.setTranslateZ(atomPos.getZ());
        }
        if(color != null){
            setColor(color);
        }
        this.tempFactor = tempFactor;
    }

    public StringProperty getName() {
        return name;
    }

    public StringProperty getLetter() {
        return letter;
    }

    public double getTempFactor() {
        return this.tempFactor;
    }

    /**
     * Set the color of this spheres material to the provided color value.
     * @param color
     */
    public void setColor(Color color){
        //this is to keep the opacity value, which might have been set.
        PhongMaterial atomMaterial = ((PhongMaterial) this.getMaterial());
        if(color != null) {
            if(atomMaterial == null) {
                atomMaterial = new PhongMaterial();
            }
            atomMaterial.setDiffuseColor(color.darker());
            atomMaterial.setSpecularPower(3.4);
            atomMaterial.setSpecularColor(color.brighter());
            this.setMaterial(atomMaterial);
        }
    }

}
