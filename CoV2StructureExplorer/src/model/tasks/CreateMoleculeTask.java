package model.tasks;

import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import model.molecules.Molecules;
import model.pdbaccess.FileParser;
import org.apache.commons.lang.NullArgumentException;

import java.io.IOException;
/**
 * This class is a Task which creates molecules from a PDB file. It expects the name of the PDB file (which is a PDB entry ID)
 * and a FileParser, which can parse PDB files.
 * The resulting Molecules class will contain different polymers, which correspond to the chains, mentioned in the file.
 * Each Polymer contains different structures, which can be Helix (HELIX section of PDB file), Sheet (SHEET section),
 * Nucleotides (just mentioned monomers in SEQRES section with 1 or 2 letters name) or Amino Acids (mentioned monomers in SEQRES section with 3 letters name).
 * Each of those structures contains 1 or more monomers. Monomers are a list of Atoms (ATOMS section).
 */
public class CreateMoleculeTask extends Task<Molecules> {

    private StringProperty filename;
    private FileParser fileParser;
    private Molecules molecules;

    public CreateMoleculeTask(StringProperty filename, FileParser fileParser){
        scheduled();
        if(filename != null){
            this.filename = filename;
        }
        else {
            //cancel task if no name is specified.
            cancel(true);
            this.setException(new NullArgumentException("Molecule can not be created from a file which is null."));
        }
        if(fileParser != null){
            this.fileParser = fileParser;
        }
        else{
            cancel(true);
            this.setException(new NullArgumentException("Molecule can not be created using a parser which is null."));
        }
        running();
    }


    /**
     * Get the Group of all visualizations of the molecule, which is described in pdb-file with name filename
     * @return - a group containing all atoms mentioned in the file with name filename as a sphere and the according bonds as cylinders;
     *          if no molecule could be read from the filename, an exception is set.
     */
    @Override
    protected Molecules call() {
        updateProgress(0, 100);                 //progress is starting

        try{
            this.molecules = this.fileParser.getMoleculeFromFile(this);
        }
        catch (IOException e){
            setException(e);
        }
        updateProgress(90, 100);
        if(molecules != null){
            molecules.centerAtOrigin();
        }
        else{
            setException(new NullArgumentException("No molecule could be created from the given file. \n Sorry, please try again later."));
            cancel(true);
        }
        updateProgress(99, 100);
        return molecules;
    }

    public String getFilename(){
        return this.filename.get();
    }

    /**
     * update the progress value of the current task to the given value as current progress value of a maximum of 100.
     * @param updateValue the value to which the progress will be set from 100, needs to be and greater than or equal to 0 and smaller than or equal to 100
     */
    public void updateProgressValue(double updateValue){
        if(updateValue >= 0 && updateValue <= 100){
            updateProgress(updateValue, 100);
        }
        //otherwise dont do anything
    }
}
