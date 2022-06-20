package model;

import javafx.geometry.Point3D;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

/**
 * basic water molecule
 */
public class WaterMolecule implements Molecule {
    private final Atom[] atoms;
    private final Point3D[] locations;
    private final List<Pair<Integer,Integer>> bonds;

    public WaterMolecule() {
        atoms=new Atom[]{new Oxygen(),new Hydrogen(),new Hydrogen()};
        locations=new Point3D[]{new Point3D(0,0,0),
                (new Point3D(-75.75,58.71,0)),
                (new Point3D(75.75,58.71,0))};
        bonds= Arrays.asList(new Pair<>(0,1),new Pair<>(0,2));
    }

    @Override
    public String getName() {
        return "Water";
    }

    @Override
    public String getFormula() {
        return "H20";
    }

    @Override
    public int getNumberOfAtoms() {
        return atoms.length;
    }

    @Override
    public Atom getAtom(int pos) {
        return atoms[pos];
    }

    @Override
    public Point3D getLocation(int pos) {
        return locations[pos];
    }

    @Override
    public List<Pair<Integer, Integer>> bonds() {
        return bonds;
    }
}
