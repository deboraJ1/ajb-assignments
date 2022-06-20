package model;

import javafx.geometry.Point3D;
import model.molecules.Molecules;
import model.molecules.Monomer;
import model.molecules.Polymer;
import model.molecules.Structure;
import view.AtomSphere;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MoleculeAnalyzer {

    // METHODS FOR CONTENT INFORMATION
    /**
     * Get all Names of chains and monomer types which can be found in the given molecules object
     * @param molecules
     * @return
     */
    public static Set<String> getAllStructureNamesFromMolecule(Molecules molecules){
        Set<String> structureNames = new HashSet<>();
        if(molecules != null) {
            List<Polymer> polymers = molecules.getPolymers();
            for (int i = 0; i < polymers.size(); i++) {
                Polymer p = polymers.get(i);
                structureNames.add(p.getUnitID());
                List<Structure> structures = p.getStructures();

                for(int j = 0; j<structures.size(); j++){
                    List<Monomer> monomers = structures.get(j).getMonomers();   //structure names are already known, not needed
                    for(int k = 0; k<monomers.size(); k++){
                        structureNames.add(monomers.get(k).getType().name());
                    }
                }
            }
        }
        return structureNames;
    }


    /**
     * Calculates the center of a part of the shown molecules, which is specified by the given list of AtomSpheres.
     * @param spheresOfSubMolecule
     * @return the center of all AtomSpheres which were given in the list spheresOfSubMolecule.
     */
    public static Point3D getCenterOfSubMolecules(List<AtomSphere> spheresOfSubMolecule){
        if(spheresOfSubMolecule != null && spheresOfSubMolecule.size() > 0){
            int numberOfAtoms = spheresOfSubMolecule.size();

            double sumX = 0; double sumY = 0; double sumZ = 0;
            for (int i = 0; i < spheresOfSubMolecule.size(); i++) {
                AtomSphere sphere = spheresOfSubMolecule.get(i);
                sumX += sphere.getTranslateX();
                sumY += sphere.getTranslateY();
                sumZ += sphere.getTranslateZ();
            }
            if(!(sumX == 0 && sumY == 0 && sumZ == 0)) { //not realistic that all spheres are at origin
                //calculate mean
                Point3D centerOfSubMolecule= new Point3D(sumX/numberOfAtoms, sumY/numberOfAtoms, sumZ/numberOfAtoms);
                return centerOfSubMolecule;
            }
        }
        return null;
    }
}
