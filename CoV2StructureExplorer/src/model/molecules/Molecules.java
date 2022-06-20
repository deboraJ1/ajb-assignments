package model.molecules;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point3D;
import javafx.util.Pair;
import model.atoms.AtomI;

import java.util.*;

public class Molecules {
    public static final double TOLERANCE = 0.015;

    private final String name;
    private List<Polymer> polymers;
    private boolean isCentralized;
    private IntegerProperty numberOfAtoms;
    private BooleanProperty hasHelix;
    private BooleanProperty hasSheets;
    private IntegerProperty numberOfPolymers;

    /**
     * Constructor of the class MoleculesList to create a new instance of this class.
     * @param name of the MoleculesList to be created; not null. can be blank if unknown
     */
    public Molecules(String name){
        if(name != null){
            //name is allowed to be blank
            this.name = name;
        }
        else{
            this.name = "";
            System.err.println("MoleculesList can not be created with null name. Empty name was used.");
        }
        this.numberOfAtoms = new SimpleIntegerProperty(0);
        this.polymers = new ArrayList<>();
        this.isCentralized = false;
        this.hasHelix = new SimpleBooleanProperty(false);
        this.hasSheets = new SimpleBooleanProperty(false);
        this.numberOfPolymers = new SimpleIntegerProperty(0);
    }

    public String getName() {
        return name;
    }

    public List<Polymer> getPolymers(){
        return polymers;
    }

    /**
     * Set the given list of Polymers as the polymers of this molecules.
     * If the given list is valid, the properties of this class will be updated accordingly.
     * @param polymers list of polymers of this molecules class.
     */
    public void setPolymers(List<Polymer> polymers){
        if(polymers != null){
            this.polymers = polymers;
            int size = 0;
            for(int i = 0; i< polymers.size(); i++){
                size += this.polymers.get(i).getMonomers().stream().mapToInt(mono -> mono.getAtoms().size()).sum();
            }
            //update other properties
            this.numberOfAtoms.set(size);
            this.hasHelix.set(getStructureIDs(StructureType.HELIX).size() > 0);
            this.hasSheets.set(getStructureIDs(StructureType.SHEET).size() > 0);
            this.numberOfPolymers.set(polymers.size());
        }
    }

    public List<AtomI> getAtoms(){
        List<AtomI> allAtoms = new ArrayList<>();
        for (Polymer polymer : this.polymers) {
            List<Monomer> monomers = polymer.getMonomers();
            for (Monomer monomer : monomers) {
                allAtoms.addAll(monomer.getAtoms());
            }
        }
        return allAtoms;
    }
    //_______________________________________________________________________________________PROPERTY GETTER

    public int getNumberOfAtoms() {
        return numberOfAtoms.get();
    }

    public IntegerProperty numberOfAtomsProperty() {
        return numberOfAtoms;
    }

    public boolean hasHelix() {
        return hasHelix.get();
    }

    public BooleanProperty hasHelixProperty() {
        return hasHelix;
    }

    public boolean hasSheets() {
        return hasSheets.get();
    }

    public BooleanProperty hasSheetsProperty() {
        return hasSheets;
    }


    public IntegerProperty numberOfPolymersProperty() {
        return numberOfPolymers;
    }

    /**
     * Centralize this molecule so that its coordinates are around the origin of the coordinate system and
     * simultaneously scale the position by 100 so that atoms are not too close to each other.
     */
    public void centerAtOrigin(){
        if(!isCentralized) {
            int numberOfAtoms = this.numberOfAtoms.getValue(); //saves requests if it is already hold locally

            Point3D sumPoint = new Point3D(0,0,0);
            for (Polymer polymer : polymers) {
                for (Point3D location : polymer.getLocations()) {
                    sumPoint = sumPoint.add(location);
                }
            }
            if(!(sumPoint.getX() == 0 && sumPoint.getY() == 0 && sumPoint.getZ() == 0)) { //otherwise no units are stored
                //calculate mean
                Point3D centerOfMolecule= new Point3D(sumPoint.getX()/numberOfAtoms, sumPoint.getY()/numberOfAtoms, sumPoint.getZ()/numberOfAtoms);

                for (Polymer polymer : polymers) {
                    for (Monomer monomer : polymer.getMonomers()) {
                        for (AtomI atom : monomer.getAtoms()) {
                            Point3D newCenter = atom.getLocation().getValue();
                            atom.setLocation(newCenter.subtract(centerOfMolecule));
                        }
                    }
                }
            }
        }
        //if already centralized, nothing to do
    }

    /**
     * Find the ranges of this Molecule in each direction
     * @return a pair of point3D where the key of the pair gives the minimum values in each direction for this molecules
     *         and the value of the pair gives the maximum values in each direction for this molecules
     */
    public Pair<Point3D, Point3D> findRanges(){
        double minX = 0, minY = 0, minZ = 0;
        double maxX = 0, maxY = 0, maxZ = 0;
        List<Point3D> locations;

        for (Polymer chain : polymers) {
            locations = chain.getLocations();

            //calculate min and max values of all locations per axis
            for (Point3D currentLocation : locations) {
                //find minimums
                if (currentLocation.getX() < minX) {
                    minX = currentLocation.getX();
                }
                if (currentLocation.getY() < minY) {
                    minY = currentLocation.getY();
                }
                if (currentLocation.getZ() < minZ) {
                    minZ = currentLocation.getZ();
                }
                if (currentLocation.getX() > maxX) {
                    maxX = currentLocation.getX();
                }
                if (currentLocation.getY() > maxY) {
                    maxY = currentLocation.getY();
                }
                if (currentLocation.getZ() > maxZ) {
                    maxZ = currentLocation.getZ();
                }
            }
        }
        return new Pair<>(new Point3D(minX, minY, minZ), new Point3D(maxX, maxY, maxZ));
    }
    //_________________________________________________________________________________METHODS TO GET STRUCTURE INFO
    /**
     * Get a list of all chain names which can be found in this molecules object
     * @return the list with all chain names
     */
    public Set<String> getChainNames(){
        Set<String> chainNames = new HashSet<>();
        for (Polymer polymer : polymers) {
            chainNames.add(polymer.getUnitID());
        }
        return chainNames;
    }

    /**
     * Get a set of all structure types which can be found in this molecules object
     * @return a set with all structure types
     */
    public Set<StructureType> getStructureTypes(){
        Set<StructureType> structs = new HashSet<>();
        for (Polymer polymer : polymers) {
            List<Structure> structures = polymer.getStructures();

            for (Structure structure : structures) {
                structs.add(structure.getStructureType());
            }
        }
        return structs;
    }

    /**
     * Get a set of all structure ids of structures with the specified type, which can be found in this molecules object
     * @param type the type for which the ids shall be searched.
     * @return a set with all ids of structures of given structure types
     */
    public Set<String> getStructureIDs(StructureType type){
        if(type != null) {
            Set<String> structIDs = new HashSet<>();
            for (Polymer polymer : polymers) {
                List<Structure> structures = polymer.getStructures();

                for (Structure structure : structures) {
                    if (structure.getStructureType().equals(type)) {
                        structIDs.add(structure.getId());
                    }
                }
            }
            return structIDs;
        }
        return null;
    }

    /**
     * Get a set of all monomer names which can be found in this molecules object
     * @return a set with all names of monomers
     */
    public Set<String> getMonomerNames(){
        Set<String> monomerNames = new HashSet<>();
        for (Polymer polymer : polymers) {
            List<Structure> structures = polymer.getStructures();

            for (Structure structure : structures) {
                List<Monomer> monomers = structure.getMonomers();

                for (Monomer monomer : monomers) {
                    monomerNames.add(monomer.getType().name());
                }
            }
        }
        return monomerNames;
    }

    /**
     * Get the number of each kind of atoms of the chain with given chain ID
     * @param chainID ID of the chain for which the number of atoms is requested; shall not be null or blank.
     * @return a HashMap where the keys are the names of atoms and the values are the respective number how often this atom occurred.
     *          might be null if chain ID is null or blank
     *          might be empty if no polymers are in this molecules class or if no polymer with the given ID can be found.
     */
    public Map<String, Integer> getAtomNumbersOfChain(String chainID){
        if(chainID != null && !chainID.isBlank()){
            Map<String, Integer> atomsPerChain = new HashMap<>();
            for (Polymer p : polymers) {
                if(p.getUnitID().equals(chainID)){
                    List<Structure> structures = p.getStructures();
                    for (Structure structure : structures) {
                        List<Monomer> monomers = structure.getMonomers();

                        for (Monomer monomer : monomers) {
                            List<AtomI> atoms = monomer.getAtoms();
                            for (AtomI atom : atoms) {
                                String atomName = atom.getName().getValue();
                                if (atomsPerChain.containsKey(atomName)) {
                                    atomsPerChain.put(atomName, atomsPerChain.get(atomName) + 1);
                                } else {
                                    atomsPerChain.put(atomName, 1);
                                }
                            }
                        }
                    }
                    break; //dont need to finish looping, right polymer was already found
                }
            }
            return atomsPerChain;
        }
        return null;
    }

}
