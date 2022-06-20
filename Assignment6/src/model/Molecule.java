package model;

import javafx.geometry.Point3D;
import javafx.util.Pair;

import java.util.List;

public interface Molecule {
    String getName();
    String getFormula();
    int getNumberOfAtoms();
    Atom getAtom(int pos);
    Point3D getLocation(int pos);
    List<Pair<Integer,Integer>> bonds();
}
