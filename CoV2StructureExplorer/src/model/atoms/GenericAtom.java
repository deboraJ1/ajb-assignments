package model.atoms;

import javafx.beans.property.*;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import java.security.InvalidParameterException;

public class GenericAtom implements AtomI {
        private final StringProperty name;
        private final StringProperty letter;
        private final Property<Point3D> location;
        private final IntegerProperty radius;
        private final Color color;
        private final double tempFactor;

    public GenericAtom(String name, String letter, Point3D location, double temeratureFactor) {
        if(name != null){
            this.name = new SimpleStringProperty(name);
        }else{
            //name is not mandatory
            this.name = new SimpleStringProperty();
        }
        if(letter != null){
            this.letter = new SimpleStringProperty(name);
        }else{
            this.letter = new SimpleStringProperty();
        }
        if (location != null) {
            this.location = new SimpleObjectProperty<>(location);
        } else {
            throw new InvalidParameterException("Point of an atom can not be null. Please specify where this Atom shall be located.");
        }
        this.tempFactor = temeratureFactor;
        //default values:
        this.radius = new SimpleIntegerProperty(45);
        this.color = Color.GREEN;
    }


    @Override
    public StringProperty getName() {
        return name;
    }

    @Override
    public StringProperty getLetter() {
        return letter;
    }

    @Override
    public IntegerProperty getRadiusPM() {
        return radius;
    }

    @Override
    public Property<Point3D> getLocation() {
        return location;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public double getTempFactor() {
        return this.tempFactor;
    }

    @Override
    public void setLocation(Point3D newPoint) {
        if(newPoint != null){
            this.location.setValue(newPoint);
        }
    }
}
