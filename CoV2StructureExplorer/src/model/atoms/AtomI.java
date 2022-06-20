package model.atoms;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.StringProperty;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;

public interface AtomI {
    StringProperty getName();
    StringProperty getLetter();
    IntegerProperty getRadiusPM();
    Property<Point3D> getLocation();
    Color getColor();
    double getTempFactor();
    void setLocation(Point3D newPoint);
}
