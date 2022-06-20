package model;

import javafx.geometry.Point3D;
import javafx.util.Pair;

import java.util.Arrays;
import java.util.List;

public class EthanolMolecule implements Molecule {
    private final Atom[] atoms;
    private final Point3D[] locations;
    private final List<Pair<Integer,Integer>> bonds;

    public EthanolMolecule() {
        atoms = new Atom[]{new Carbon(), new Hydrogen(), new Hydrogen(),
                            new Oxygen(), new Hydrogen(),
                            new Carbon(), new Hydrogen(), new Hydrogen(), new Hydrogen()};
        locations = new Point3D[]{new Point3D(1.247000,2.254000,108.262000), new Point3D(-49.334000,93.505000, 144.716000), new Point3D( 105.522000,4.512000,144.808000),
                                new Point3D(-66.442000,-115.471000,156.909000), new Point3D(-64.695000,-112.346000, 254.219000),
                                new Point3D(-0.894000,-1.624000,-43.421000), new Point3D(49.999000,86.726000,-84.481000), new Point3D(-104.310000,-2.739000,-80.544000), new Point3D(50.112000,-91.640000,-80.440000)};
        bonds = Arrays.asList(new Pair<>(0,1), new Pair<>(0,2), new Pair<>(0,3), new Pair<>(3,4), new Pair<>(0,5), new Pair<>(5,6), new Pair<>(5,7), new Pair<>(5,8));
    }

    @Override
    public String getName() {
        return "Ethanol";
    }

    @Override
    public String getFormula() {
        return "C2H50H";
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