package model;
import javafx.scene.paint.Color;

public class Carbon implements Atom {
    @Override
    public String getName() {
        return "Carbon";
    }

    @Override
    public String getLetter() {
        return "C";
    }

    @Override
    public int getRadiusPM() {
        return 61;
    }

    @Override
    public Color getColor() {
        return Color.GRAY;
    }
}
