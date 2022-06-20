package model;

import javafx.scene.paint.Color;

public class Hydrogen implements Atom {
    @Override
    public String getName() {
        return "Hydrogen";
    }

    @Override
    public String getLetter() {
        return "H";
    }

    @Override
    public int getRadiusPM() {
        return 31;
    }

    @Override
    public Color getColor() {
        return Color.WHITE;
    }
}
