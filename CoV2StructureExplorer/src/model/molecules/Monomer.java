package model.molecules;

import javafx.geometry.Point3D;
import javafx.util.Pair;
import model.atoms.AtomI;
import model.atoms.Carbon;
import model.atoms.CarbonPosition;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a Monomer of a Molecule. A monomer can be a nucleotide or an amino acid.
 * A representation of this class holds the information if it is an amino acid or not (then it is a nucleotide).
 */
public class Monomer {

    private final int seqNumber; //can be sheet ID if belongs to structure SHEET, helix ID or chain ID if just nucleotide
    private final List<AtomI> atoms;
    private final AminoAcid_Nucleotide type;
    private final List<Pair<Point3D, Point3D>> bonds;

    public Monomer(int sequenceNumber, AminoAcid_Nucleotide type) throws InvalidParameterException{
        if(sequenceNumber >= 0){
            this.seqNumber = sequenceNumber;
        }
        else{
            throw new InvalidParameterException("Monomer with sequence Number smaller than zero can not be created.");
        }

        if(type != null){
            this.type = type;
        }
        else{
            this.type = AminoAcid_Nucleotide.OTHER;
            System.err.println("Monomer of type null is not valid, default type was used..");
        }
        this.atoms = new ArrayList<>();
        this.bonds = new ArrayList<>();
    }

    /**
     * Get the number of this Monomer in the sequence of monomers of the polymer this monomer belongs to
     * @return  the sequence number of this monomer within the polymer, this Monomer belongs to.
     */
    public int getSequenceNumber() {
        return this.seqNumber;
    }

    /**
     * Get the type of this monomer. A monomer can be an Amino Acid or a Nucleotide.
     * @return the type of this monomer, which can be one of the different Amino Acid types or one of the Nucleotide types
     */
    public AminoAcid_Nucleotide getType(){return this.type;}

    public void addAtom(AtomI atom){
        if(atom != null){
            this.atoms.add(atom);
        }
    }

    /**
     * Get all Atoms, which are part of this Monomer
     * @return the list of all Atoms
     */
    public List<AtomI> getAtoms() {
        return atoms;
    }

    /**
     * Get all locations of all atoms, which are in this monomer
     * @return the list of all Points of atoms
     */
    public List<Point3D> getLocations() {
        List<Point3D> locations = new ArrayList<>();
        for (AtomI atom : atoms) {
            locations.add(atom.getLocation().getValue());
        }
        return locations;
    }

    /**
     * Get all bonds of this monomers atoms. Each bond is given by a pair of 3D points which define the start and end point of one bond.
     * @return a list of pairs of 3D points where each pair corresponds to a bond between to atoms.
     * Key and Value of each pair are the center points of starting and ending atom of the bond
     */
    public List<Pair<Point3D, Point3D>> getBonds(){
        if(bonds.size() == 0){
            for(int i = 0; i<atoms.size(); i++){
                for(int k = i+1; k< atoms.size(); k++){
                    AtomI firstAtom = atoms.get(i);
                    AtomI secondAtom = atoms.get(k);
                    double distanceThresh = (firstAtom.getRadiusPM().get() + secondAtom.getRadiusPM().get())* Molecules.TOLERANCE;
                    double distance = firstAtom.getLocation().getValue().distance(secondAtom.getLocation().getValue());
                    if(distance <= distanceThresh){
                        this.bonds.add(new Pair<>(firstAtom.getLocation().getValue(), secondAtom.getLocation().getValue()));
                    }
                }
            }
        }
        return this.bonds;
    }

    /**
     * Get the atom of this Monomer which is the atom with the given position in ribbon representation.
     * If this monomer is an amino acid, the guiding atom is C alpha and the twisting atom is C beta
     * if it is a nucleotide, the guiding atom is C5' and the twisting atom is C1'.
     * @param position the position of the searched Atom which can be Guide, Twist, Opposite or Residue but as each monomer
     *                is supposed to have maximum one guiding, one twisting and one opposite Carbon there is no sense to ask for residue postion.
     *                 More than one Carbon can be residue position per monomer but if requested only the first one will be reported.
     * @return the Position of the atom which is searched for with the given CarbonPosition in ribbon view. null if position was null.
     */
    public Point3D getRibbonPosition(CarbonPosition position){
       if(position != null) {
           for (AtomI atom : this.getAtoms()) {
               if (atom.getClass().equals(Carbon.class)) {
                   //other atoms are never relevant for ribbon.
                   if (((Carbon) atom).getPosition().equals(position)) {
                       return atom.getLocation().getValue();
                   }
               }
           }
       }
       return null;
    }

}
