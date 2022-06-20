package view;

import javafx.beans.property.Property;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.scene.Cursor;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

/**
 * Class MouseInteraction provides functionality to install rotation of objects according to mouse interaction.
 */
public class MouseInteraction {
    private static double x;
    private static double y;
    private static final double factor = 0.7;

    /**
     * Installs rotation of a property.
     * @param pane - the pane in which mouse interaction distances will be calculated
     * @param figureTransformProperty - property to be changed according to mouse movement within pane.
     */
    public static void installRotate(Pane pane, Property<Transform> figureTransformProperty) {
        //set current scene position when mouse pressed
        pane.setOnMousePressed(e -> {
            x = e.getSceneX();
            y = e.getSceneY();
        });

        pane.setOnMouseDragged(e -> {
            pane.setCursor(Cursor.CLOSED_HAND);

            //delta from mouse pressed to dragged position
            Point2D delta = new Point2D(e.getSceneX() - x, e.getSceneY() - y);
            Point3D axisOrthogonalToDrag = new Point3D(delta.getY(), -delta.getX(), 0);
            Rotate rotate = new Rotate(factor * delta.magnitude(), axisOrthogonalToDrag);

            //add current rotation to previous rotation state
            figureTransformProperty.setValue(rotate.createConcatenation(figureTransformProperty.getValue()));

            x = e.getSceneX();
            y = e.getSceneY();
            e.consume();
        });

        pane.setOnMouseReleased(e -> pane.setCursor(Cursor.DEFAULT));
    }

}
