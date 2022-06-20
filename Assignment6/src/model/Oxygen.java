package model;

import javafx.scene.paint.Color;

public class Oxygen implements Atom{
    @Override
    public String getName() {
        return "Oxygen";
    }

    @Override
    public String getLetter() {
        return "O";
    }

    @Override
    public int getRadiusPM() {
        return 66;
    }

    @Override
    public Color getColor() {
        return Color.RED;
    }
}
