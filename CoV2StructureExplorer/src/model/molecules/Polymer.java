package model.molecules;

import javafx.geometry.Point3D;

import java.util.ArrayList;
import java.util.List;

public class Polymer {

    private final String unitID;          //chain ID in PDB file
    private final List<Structure> structures;
    private final List<Point3D> locations;
    private final List<Monomer> monomers;

    public Polymer(String unitID){
        if(unitID != null){
            this.unitID = unitID;
        }
        else{
            this.unitID = "X";
            System.err.println("Tried to create a polymer with null ID, was set to X.");
        }
        this.structures = new ArrayList<>();
        this.locations = new ArrayList<>();
        this.monomers = new ArrayList<>();
    }


    public String getUnitID() {
        return this.unitID;
    }

    public List<Structure> getStructures() {
        return structures;
    }

    /**
     * Adds a structure to this Polymer.
     * @param structure the structure to be added; shall not be null. Structures can be Helixes, Sheets, Nucleotides or Other.
     */
    public void addStructure(Structure structure){
        if(structure != null){
            this.structures.add(structure);
        }
    }

    /**
     * Get all Monomers of this polymer
     * @return a list of all monomers, which belong to this polymer
     */
    public List<Monomer> getMonomers(){
        if(monomers.size() == 0) {
            structures.forEach(structure -> monomers.addAll(structure.getMonomers()));
        }
        return monomers;
    }


    public List<Point3D> getLocations() {
        if(locations.size() == 0) {
            //then it was not yet calculated, but there should not be added anything after creating whole molecule
            for (Monomer monomer : getMonomers()) {
                locations.addAll(monomer.getLocations());
            }
        }
        return locations;
    }

}
