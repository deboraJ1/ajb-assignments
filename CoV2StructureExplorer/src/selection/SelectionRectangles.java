package selection;

import javafx.beans.InvalidationListener;
import javafx.beans.WeakInvalidationListener;
import javafx.beans.property.Property;
import javafx.geometry.Bounds;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape3D;
import view.AtomSphere;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class can be used to set up a selection visualization, meaning that everything except the selected node
 * is shown in a semi transparent way.
 */
public class SelectionRectangles {

    private Pane selectionPane;
    private List<Property> properties;
    private final Map<AtomSphere, Rectangle> shape2Rectangle;

    public SelectionRectangles(Pane selectionPane, List<Property> properties) {
        shape2Rectangle = new HashMap<>();
        if(selectionPane != null){
            this.selectionPane = selectionPane;
        }
        else{
            System.err.println("Selection Rectangles can not be created with a selection pane, which is null.");
        }
        if(properties != null){
            this.properties = properties;
        }
    }

    /**
     * Create a bounding box around a sphere of an atom in the correct correct 3D coordinates for the resulting 2D shape
     * @param sphereOfAtom AtomSphere representing an atom around which the rectangle shall be created.
     * @return the rectangle which can be used to mark sphereOfAtom being selected
     */
    public Rectangle createBoundingBoxWithBinding(AtomSphere sphereOfAtom){
        if(selectionPane != null && sphereOfAtom != null && properties != null) {
            Bounds boundsScreen = sphereOfAtom.localToScreen(sphereOfAtom.getBoundsInLocal());
            Bounds paneBoundScreen = selectionPane.localToScreen(selectionPane.getBoundsInLocal());
            double xScene = boundsScreen.getMinX() - paneBoundScreen.getMinX();
            double yScene = boundsScreen.getMinY() - paneBoundScreen.getMinY();
            Rectangle rectangle = new Rectangle(xScene, yScene, boundsScreen.getWidth(), boundsScreen.getHeight());

            //make transparent in middle:
            rectangle.setFill(new Color(1, 1, 0, 0.1));
            rectangle.setStroke(Color.GOLDENROD);

            InvalidationListener listener = a -> updateRectangle(rectangle, sphereOfAtom);
            for(Property property : properties){
                property.addListener(new WeakInvalidationListener(listener));
            }
            rectangle.setUserData(listener);

            shape2Rectangle.put(sphereOfAtom, rectangle);
            return rectangle;
        }
        return null;
    }

    private void updateRectangle(Rectangle rectangle, Shape3D sphereOfAtom) {
        if(selectionPane != null && sphereOfAtom != null && rectangle != null) {
            Bounds boundsScreen = sphereOfAtom.localToScreen(sphereOfAtom.getBoundsInLocal());
            Bounds paneBoundScreen = selectionPane.localToScreen(selectionPane.getBoundsInLocal());
            double xScene = boundsScreen.getMinX() - paneBoundScreen.getMinX();
            double yScene = boundsScreen.getMinY() - paneBoundScreen.getMinY();
            rectangle.setX(xScene);
            rectangle.setY(yScene);
            rectangle.setWidth(boundsScreen.getWidth());
            rectangle.setHeight(boundsScreen.getHeight());
            rectangle.setFill(new Color(1, 1, 0, 0.1));
            rectangle.setStroke(Color.GOLDENROD);
        }
    }
    /**
     * Get the associated rectangle of the given shape.
     * @param atom shape of an atom to which the rectangle belongs
     * @return the rectangle which is associated with the given atom spheres object
     */
    public Rectangle getRectangleOfShape(AtomSphere atom){
        if(this.shape2Rectangle != null && atom != null){
            return this.shape2Rectangle.get(atom);
        }
        return null;
    }

    /**
     * Removes the associated rectangle of the given shape and returns the object which was removed.
     * @param atom shape of an atom to which the rectangle belongs which shall be removed
     * @return the rectangle which is removed
     */
    public Rectangle removeRectangleOfShape(AtomSphere atom){
        Rectangle rect = getRectangleOfShape(atom);
        if(rect != null){
            this.shape2Rectangle.remove(atom);
            return rect;
        }
        return null;
    }
}
