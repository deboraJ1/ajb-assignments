package model.atoms;

import javafx.beans.property.*;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

import java.security.InvalidParameterException;

public class Carbon implements AtomI {
    private final StringProperty name;
    private final StringProperty letter;
    private final Property<Point3D> location;
    private CarbonPosition position;
    private final IntegerProperty radius;
    private final Color color;
    private final double tempFactor;

    public Carbon(String letter, CarbonPosition position,  Point3D location, double temperatureFactor) {
        if(letter != null && !letter.isBlank()){
            this.letter = new SimpleStringProperty(letter);
        }
        else{
            this.letter = new SimpleStringProperty("C");
        }
        if (location != null) {
            this.location = new SimpleObjectProperty<>(location);
        }
        else{
            throw new InvalidParameterException("Point of an atom can not be null. Please specify where this Carbon shall be located.");
        }
        if(position != null){
            this.position = position;
        }
        else{
            this.position = CarbonPosition.RESIDUE;
        }
        this.tempFactor = temperatureFactor;
        //default:
        this.name = new SimpleStringProperty("Carbon");
        this.radius =  new SimpleIntegerProperty(67);
        this.color = Color.GRAY;
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
    public void setLocation(Point3D newPoint) {
        if(newPoint != null){
            this.location.setValue(newPoint);
        }
    }

    @Override
    public double getTempFactor() {
        return this.tempFactor;
    }

    public CarbonPosition getPosition() {
        return position;
    }
}