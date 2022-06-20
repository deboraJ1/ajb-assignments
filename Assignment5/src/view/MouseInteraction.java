package view;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

/**
 * implements simple mouse interaction
 */
public class MouseInteraction {
    private static double x;
    private static double y;
    //TODO FIX when mouse interaction is performed, scaling is not kept persistent even though 'to scale' is selected !

    /**
     * install mouse event handlers to implement click-drag of nodes
     *
     * @param node      the target node
     * @param xProperty the x-property to be modified (e.g. translateXProperty() or xProperty())
     * @param yProperty the y-property to modified
     */
    public static void install(Node node, DoubleProperty xProperty, DoubleProperty yProperty) {
        node.setOnMousePressed(e -> {
            x = e.getSceneX();
            y = e.getSceneY();
            node.setEffect(new DropShadow(5, Color.ORANGE));
        });

        node.setOnMouseDragged(e -> {
            var dx = e.getSceneX() - x;
            var dy = e.getSceneY() - y;
            xProperty.set(xProperty.get() + dx);
            yProperty.set(yProperty.get() + dy);
            x = e.getSceneX();
            y = e.getSceneY();
        });

        node.setOnMouseReleased(e -> node.setEffect(null));
    }
}
