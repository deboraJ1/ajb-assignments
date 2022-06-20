

package model.pdbaccess;

import javafx.geometry.Point3D;
import model.atoms.*;
import model.molecules.*;
import model.tasks.CreateMoleculeTask;
import org.apache.commons.lang.NullArgumentException;

import java.io.*;
import java.util.*;

/**
 * This is a helper class providing different methods to write and read PDB files, which were downloaded e.g. by the PDBWebClient.
 */
public class FileParser {

    public static final String DEFAULT_STORING_DIRECTORY = System.getProperty("user.dir") + File.separator + "CoV2StructureExplorer" + File.separator + "resources" + File.separator + "pdbFiles" + File.separator;
    public static final String FILE_EXTENSION = ".pdb"; //in case one would like to change later.

    private BufferedReader reader;
    private List<Polymer> chains;                       //these are the chains from the file
    private Molecules molecules;                //contains all molecules, can also be only one
    private Map<String, List<Monomer>> monomersOfChain;

    /**
     * Create a file with default fileExtension from the given input Stream. The file will be saved in the default folder with the name of the file will being the pdbID.
     * @param pdbInputStream - the stream to be written in a file. Shall not be null.
     * @param pdbID - the entryID of the file, represented in the inputStream; will be the name of the file; shall not be null nor blank
     * @throws IOException - if an error occurs reading the stream or writing the file
     * @throws NullArgumentException - if input stream is null or pdbID is null or blank.
     */
    public static void createFileFromStream(InputStream pdbInputStream, String pdbID) throws IOException, NullArgumentException{
        if(pdbInputStream != null && pdbID != null && !pdbID.isBlank()){
            //create file
            String path =  DEFAULT_STORING_DIRECTORY + pdbID + FILE_EXTENSION;
            File pdbFile = new File(path);
            //if file was non existing before
            if(pdbFile.createNewFile()){
                //create reader and writer

                BufferedReader reader = new BufferedReader(new InputStreamReader(pdbInputStream));
                BufferedWriter writer = new BufferedWriter(new FileWriter(pdbFile));

                //read line by line from input Stream and write to file
                String nextLine = reader.readLine();
                while(nextLine != null){
                    //skip all those lines: HEL, JRNL, REMARK, REVDAT, HETNAM
                    if(!nextLine.startsWith("HEL") && !nextLine.startsWith("JRNL") && !nextLine.startsWith("REMARK") && !nextLine.startsWith("REVDAT") && !nextLine.startsWith("HETNAM")) {
                        writer.write(nextLine + "\n");
                    }
                    nextLine = reader.readLine();
                }
                //end of file was reached
                reader.close();
                writer.close();
            }
        }
        else{
            throw new NullArgumentException("File can not be created when input Stream is null or PDB ID is null or blank.");
        }
    }

    /**
     * Reads a file with file name fileName from the default location and parses it to a string which can be displayed e.g.
     * @param fileName - the file name of the file to be read without file extension. Default file Extension will be used.
     *                 not null and not blank
     * @return a String representation of the file which can be e.g. displayed somewhere, empty string if fileName was null or blank
     * @throws IOException if the file with given filename was not found in the default directory or if an I/O error occurred while reading.
     */
    public static String getContentOfFile(String fileName) throws IOException {
        StringBuilder fileString = new StringBuilder();

        if(fileName != null && !fileName.isBlank()){
            if(!fileName.endsWith(".pdb")){
                fileName = fileName +FILE_EXTENSION;
            }
            BufferedReader reader = new BufferedReader(new FileReader(DEFAULT_STORING_DIRECTORY + fileName));
            String nextLine = reader.readLine();
            while(nextLine != null) {
                fileString.append(nextLine).append(" \n ");
                nextLine = reader.readLine();
            }
        }
        return fileString.toString();
    }

    private void resetLists(){
        //init
        this.monomersOfChain = new HashMap<>();
        this.chains = new ArrayList<>();
    }


    /**
     * Create a molecule according to the description given in the filename with name filename.
     * @param task - the task which called this method. model.tasks progress will be updated and the task needs to contain a filename of a file containing information about single atoms of the molecule to be created.
     * @return - the molecule which contains all atoms mentioned in the specified file from task
     * @throws IOException - if an error occurs reading the file
     */
    public Molecules getMoleculeFromFile(CreateMoleculeTask task) throws IOException {
        if(task != null) {
            String filename = task.getFilename();

            if (filename != null && !filename.isBlank()) {
                resetLists();
                this.molecules = new Molecules(filename);
                List<String> moleculeNames = new ArrayList<>();
                this.reader = new BufferedReader(new FileReader(DEFAULT_STORING_DIRECTORY + filename + FILE_EXTENSION));

                String nextLine = this.reader.readLine();
                while (nextLine != null) {
                    while (nextLine.startsWith("COMPND")) { //get macromolecule names and names of polymers, belonging to each macromolecule

                        if (nextLine.contains("MOLECULE:")) {
                            String moleculeName = nextLine.substring(11, 80).trim().replace(";", "");
                            while(!nextLine.contains("CHAIN")) {
                                nextLine = this.reader.readLine();
                            }
                            if (nextLine.contains("CHAIN:")) {
                                // get all monomers by removing end of line sign: ; and splitting at separator: ,
                                String[] ids = nextLine.substring(18, 80).trim().replace(";", "").split(",");

                                //store molecules name and belonging monomer ids
                                if (!moleculeName.isBlank() && ids.length > 0) {
                                    moleculeNames.add(moleculeName);
                                    for (String id : ids) {//init found chains
                                        this.chains.add(new Polymer(id.trim())); //init polymers, which are mentioned for later sections
                                    }
                                } else {
                                    System.out.println("There was a molecule with empty or null name or chain IDs were empty");
                                }
                            }
                        }
                        nextLine = this.reader.readLine();
                        task.updateProgressValue(5);
                    }
                    //records SOURCE, KEYWDS, EXPDTA, NUMMDL, MDLTYP, AUTHOR, REVDAT, SPRSDE, JRNL, DBREF, SEQADV are not considered

                    while (nextLine.startsWith("SEQRES")) {    //parse details of polymers
                        String chainID = nextLine.substring(11, 12).trim();
                        this.monomersOfChain.put(chainID, getAllMonomersFromChain(chainID, nextLine));                      // store all monomers, will be put into structures later

                        nextLine = this.reader.readLine(); // next molecule will be read (or start new section)
                        task.updateProgressValue(15);
                    }

                    while (nextLine.startsWith("HELIX")) {
                        String helixID = nextLine.substring(11, 14).trim();
                        String initChainID = nextLine.substring(19, 20).trim().toUpperCase();                               //this does not change within one sheet
                        int chainPos = findChainPosition(initChainID);
                        List<Monomer> helixMonomers = parseHelix(helixID, nextLine, initChainID);

                        //                here is additionally -1 because positions p % 2 == 0 define start and p % 2 == 1 define stop Monomer --> always two monomers are processed
                        for(int i = 0; i < helixMonomers.size()-1; i++){
                            Monomer startMono = helixMonomers.get(i);
                            Monomer stopMono = helixMonomers.get(i+1);
                            if(this.monomersOfChain.containsKey(initChainID) && startMono != null && stopMono != null) {
                                List<Monomer> allChainMonomers = this.monomersOfChain.get(initChainID);
                                //find positions of searched monomers in list of according chain
                                int start = findMatchingMonomerPosition(allChainMonomers, startMono.getType().name(), startMono.getSequenceNumber());
                                int stop = findMatchingMonomerPosition(allChainMonomers, stopMono.getType().name(), stopMono.getSequenceNumber());
                                if (start >= 0 && start < stop) {
                                    Structure helix = new Structure(helixID, StructureType.HELIX);
                                    helix.setMonomers(allChainMonomers.subList(start, stop + 1));                      //add all monomers between start and stop to a sheet

                                    this.chains.get(chainPos).addStructure(helix);
                                }
                            }
                            i++; // increase by 2 because Monomers are processed pairwise.
                        }

                        nextLine = this.reader.readLine(); //next helix will be read (or start new section)
                        task.updateProgressValue(20);
                    }

                    while (nextLine.startsWith("SHEET")) {
                        String sheetID = nextLine.substring(11, 14).trim().toUpperCase();
                        String initChainID = nextLine.substring(21, 22).trim().toUpperCase();                               //this does not change within one sheet
                        int chainPos = findChainPosition(initChainID);
                        List<Monomer> sheetMonomers = parseSheet(sheetID, nextLine, initChainID);

                        //here is additionally -1 because positions p % 2 == 0 define start and p % 2 == 1 define stop Monomer --> always two monomers are processed
                        for(int i = 0; i < sheetMonomers.size()-1; i++){
                            Monomer startMono = sheetMonomers.get(i);
                            Monomer stopMono = sheetMonomers.get(i+1);
                            if(this.monomersOfChain.containsKey(initChainID) && startMono != null && stopMono != null) {
                                List<Monomer> allChainMonomers = this.monomersOfChain.get(initChainID);
                                //find positions of searched monomers in list of according chain
                                int start = findMatchingMonomerPosition(allChainMonomers, startMono.getType().name(), startMono.getSequenceNumber());
                                int stop = findMatchingMonomerPosition(allChainMonomers, stopMono.getType().name(), stopMono.getSequenceNumber());
                                if (start >= 0 && start < stop) {
                                    Structure sheet = new Structure(sheetID, StructureType.SHEET);
                                    sheet.setMonomers(allChainMonomers.subList(start, stop + 1));                      //add all monomers between start and stop to a sheet

                                    this.chains.get(chainPos).addStructure(sheet);
                                }
                            }
                            i++; // increase by 2 because Monomers are processed pairwise.
                        }

                        nextLine = this.reader.readLine(); //next sheet will be read (or start new section)
                        task.updateProgressValue(25);
                    }

                    //this part might be skipped completely if there is only one model
                    //if it is there: only look at first model as they all describe same model.
                    if (nextLine.startsWith("MODEL")) { //ends when corresponding ENDMDL appears
                        nextLine = this.reader.readLine(); //to get first ATOM line
                        task.updateProgressValue(30);
                    }
                    while(nextLine.startsWith("ATOM")) {
                        parseAtoms(nextLine);           //parses until does not start with ATOM anymore
                        nextLine = reader.readLine();   //if TER, this will bring next line with ATOM again from next sheet.
                        task.updateProgressValue(50);
                    }

                    nextLine = this.reader.readLine();
                }
                task.updateProgressValue(70);
                if (this.chains.size() > 0) {
                    StringBuilder macroMolName = new StringBuilder();
                    for(String molName : moleculeNames){
                        macroMolName.append(molName).append(", ");
                    }
                    macroMolName = new StringBuilder(macroMolName.substring(0, macroMolName.length() - 2)); //remove last ,

                    // else: no name stored for macromolecule...
                    molecules = new Molecules(macroMolName.toString());
                    molecules.setPolymers(this.chains);
                    System.out.printf("Molecules %s are created. %n", macroMolName);
                }
                task.updateProgressValue(90);
            }
            return molecules;
        }
        return null;
    }

    /**
     * Parse all following lines, starting from nextline, which have HELIX in the beginning as long as there is no line starting with anything else.
     * All found Helix monomers are collected in a list which will be returned.
     * @param helixID - the helix ID for which all following lines shall be parsed.
     * @param nextLine - the line from which the reader is starting.
     * @param initChainID the chainID in which the starting monomer of this sheet is located, if the end monomer of this sheet turns out to be in another chain, the according monomer will be initialized with null.
     * @return a list of all initialized monomers, which could be found, can contain null values if inconsistencies are found like explained above.
     * @throws IOException if an error occurs when reading.
     */
    private List<Monomer> parseHelix(String helixID, String nextLine, String initChainID) throws IOException {
        List<Monomer> parsedMonomers = null;
        if(helixID != null && nextLine != null && initChainID != null){
            parsedMonomers = new ArrayList<>();
            //                                                                         check this, because after sheet comes LINK which might not be realized otherwise.
            while (nextLine.substring(11, 14).trim().toUpperCase().equals(helixID) && nextLine.startsWith("HELIX")) {       // all monomers belong to this sheet.
                String initMonomerName = nextLine.substring(15, 18).trim().toUpperCase();
                int initSeqNumber = Integer.parseInt(nextLine.substring(21, 25).trim());
                // add starting monomer of this helix
                parsedMonomers.add(new Monomer(initSeqNumber, AminoAcid_Nucleotide.get(initMonomerName)));

                //continue with end monomer if it has same initChainID:
                if (nextLine.substring(31, 32).trim().toUpperCase().equals(initChainID)) {
                    String endMonomerName =  nextLine.substring(27, 30).trim().toUpperCase();
                    int endSeqNumber = Integer.parseInt(nextLine.substring(33, 37).trim());
                    parsedMonomers.add(new Monomer(endSeqNumber, AminoAcid_Nucleotide.get(endMonomerName)));
                }
                else {
                    //this should not happen; add null to keep list consistent (iterating start and stop monomers)
                    parsedMonomers.add(null);
                    System.err.println("Helix end monomer is supposed to be in the same polymer as the helix start monomer. For helix " + helixID + " polymers IDs differed.");
                }
                nextLine = this.reader.readLine();
            }
        }
        return parsedMonomers;
    }

    /**
     * Parse all following lines, starting from nextline, which have SHEET in the beginning as long as there is no line starting with anything else.
     * All found Sheet monomers are collected in a list which will be returned.
     * @param sheetID - the sheet ID for which all following lines shall be parsed.
     * @param nextLine - the line from which the reader is starting.
     * @param initChainID the chainID in which the starting monomer of this sheet is located, if the end monomer of this sheet turns out to be in another chain, the according monomer will be initialized with null.
     * @return a list of all initialized monomers, which could be found, can contain null values if inconsistencies are found like explained above.
     * @throws IOException if an error occurs when reading.
     */
    private List<Monomer> parseSheet(String sheetID, String nextLine, String initChainID) throws IOException {
        List<Monomer> parsedMonomers = null;
        if(sheetID != null && nextLine != null && initChainID != null){
            parsedMonomers = new ArrayList<>();
            //                                                                         check this, because after sheet comes LINK which might not be realized otherwise.
            while (nextLine.substring(11, 14).trim().toUpperCase().equals(sheetID) && nextLine.startsWith("SHEET")) {       // all monomers belong to this sheet.
                String monomerName = nextLine.substring(17, 20).trim().toUpperCase();
                int initSeqNumber = Integer.parseInt(nextLine.substring(22, 26).trim());    // for atom reference
                // add starting monomer of this sheet side
                parsedMonomers.add(new Monomer(initSeqNumber, AminoAcid_Nucleotide.get(monomerName)));

                //continue with end monomer if it has same initChainID:
                if (nextLine.substring(32, 33).trim().toUpperCase().equals(initChainID)) {
                    String endMonomerName = nextLine.substring(28, 31).trim().toUpperCase();
                    int endSeqNumber = Integer.parseInt(nextLine.substring(33, 37).trim());
                    boolean isParallelToPrevious = (Integer.parseInt(nextLine.substring(38, 40).trim()) == 1); // maybe not needed
                    parsedMonomers.add(new Monomer(endSeqNumber, AminoAcid_Nucleotide.get(endMonomerName)));
                }
                else {
                    //this should not happen; add null to keep list consistent (iterating start and stop monomers)
                    parsedMonomers.add(null);
                    System.err.println("Sheets end monomer is supposed to be in the same polymer as the sheets start monomer. For sheet " + sheetID + " polymers IDs differed.");
                }
                nextLine = this.reader.readLine();
            }
        }
        return parsedMonomers;
    }

    /**
     * Parse all following lines, starting from nextline, starting with ATOM as long as there is no line starting with anything else.
     * @param nextLine - the line from where atoms shall be parsed. Must start with ATOM, otherwise nothing will be done.
     *                 All parsed atoms are then automatically added to the according monomer which matches monomer name and molecule ID, given in the according line.
     * @throws IOException if an error occurs while reading.
     */
    private void parseAtoms(String nextLine) throws IOException {
        // this part is mandatory! All atoms from previous parts: sheets, models, monomers are specified here
        while(nextLine!= null && nextLine.startsWith("ATOM")) {
            String atomName = nextLine.substring(12, 16).trim().toUpperCase();
            char locationIndicator = nextLine.charAt(16);
            // only parse one orientation of this molecule --> if location orientation is e.g. B it were the second variant and shall not be parsed.
            if(locationIndicator == 'A' || locationIndicator == ' ') {
                String monomerName = nextLine.substring(17, 20).trim().toUpperCase();
                String chainID = nextLine.substring(21, 22).trim().toUpperCase();
                int resSeqNumber = Integer.parseInt(nextLine.substring(22, 26).trim());
                double x = Double.parseDouble(nextLine.substring(30, 38).trim());
                double y = Double.parseDouble(nextLine.substring(38, 46).trim());
                double z = Double.parseDouble(nextLine.substring(46, 54).trim());
                double tempFactor = Double.parseDouble(nextLine.substring(60, 66).trim());
                String element = nextLine.substring(77).trim().toUpperCase();

                AtomI atom;
                Point3D location = new Point3D(x, y, z);
                switch (atomName.substring(0, 1)) { //only check first letter here
                    case "N" -> atom = new Nitrogen(location, tempFactor);
                    case "C" -> {
                        CarbonPosition position;
                        switch (atomName){
                            case "CA":  //for amino acids
                            case "C5'": //for nucleotides
                                position = CarbonPosition.GUIDE;
                                break;
                            case "CB": //for amino acids
                            case "C1'": //for nucleotides
                                position = CarbonPosition.TWIST;
                                break;
                            case "CO": position = CarbonPosition.OPPOSITE;
                                break;
                            default: position = CarbonPosition.RESIDUE;
                        }
                        atom = new Carbon(atomName, position, location, tempFactor);
                    }
                    case "H" -> atom = new Hydrogen(location, tempFactor);
                    case "O" -> atom = new Oxygen(location, tempFactor);
                    case "S" -> atom = new Sulfur(location, tempFactor);
                    case "P" -> atom = new Phosphor(location, tempFactor);
                    default -> {
                        System.out.println("GenericAtom " + atomName + " was found and created.");
                        atom = new GenericAtom(atomName, element, new Point3D(x, y, z), tempFactor);
                    }
                }
                boolean successful = addAtomToMonomer(chainID, monomerName, resSeqNumber, atom);
                if (!successful) {
                    System.err.println("Atom " + element + " could not be added to chain " + chainID + ", Monomer " + monomerName + " (" + resSeqNumber + ")");
                }
            }
            nextLine = reader.readLine();
        }
    }

    /**
     * Adds the given Atom to the monomer which is in chain with ID chain ID and has the name monomerName.
     * @param chainID the chain ID in which the given atom can be found
     * @param monomerName the monomer name of the monomer in which the given atom can be found
     * @param resSeqNumber sequence number of the monomer with name monomerName in which the given atom can be found according to the ATOM section.
     *                     CAUTION: this number shall be the number of the PDB file, which starts counting at 1,
     *                     therefore in the final Polymer, this monomer will be found at position resSeqNumber -1 but the number within the monomer will always refer to the PDB counting.
     *                     This number might be incorrect as some PDB files contain inconsistencies. If at this position of the stored Monomer names
     *                     no monomer with monomerName can be found, the monomer with the same name which has a sequence Number closest to the given one will be used to add the atom.
     * @param atom the atom to be added to the monomer specified with the given other parameters
     * @return a boolean value if the given atom could be added successfully to the specified monomer.
     */
    private boolean addAtomToMonomer(String chainID, String monomerName, int resSeqNumber, AtomI atom) {
        if(chainID != null && !chainID.isBlank() && monomerName != null && !monomerName.isBlank() && resSeqNumber > 0 && atom != null) {
            int chainPos = findChainPosition(chainID);
            if(chainPos >= 0){                                                           //otherwise chain was not found
                List<Structure> structures = this.chains.get(chainPos).getStructures();
                for (int j = 0; j < structures.size(); j++) {
                    Structure struct = structures.get(j);

                    List<Monomer> monomers = struct.getMonomers();
                    int monomerPos = findMatchingMonomerPosition(monomers, monomerName, resSeqNumber);
                    if(monomerPos >= 0){
                        //closest monomer in structure was found
                        if(monomers.get(monomerPos).getSequenceNumber() == resSeqNumber){
                            //this is exactly the monomer we are looking for
                            this.chains.get(chainPos).getStructures().get(j).getMonomers().get(monomerPos).addAtom(atom);
                            return true;
                        }
                        //else: this is the closes monomer matching the searched one but not the actual one
                    }
                }
                //structure was not yet created:
                Structure structure = new Structure("DAIJ", StructureType.NUCLEOTIDE);
                if(monomerName.length() == 3){
                    structure = new Structure("N", StructureType.OTHER);
                }
                Monomer m = new Monomer(resSeqNumber, AminoAcid_Nucleotide.get(monomerName));
                m.addAtom(atom);
                structure.getMonomers().add(m);
                this.chains.get(chainPos).addStructure(structure);
                 return true;
            }
        }
        return false;
    }


    /**
     * Parses the following lines as long as the lines contain the same moleculeID, which was provided.
     * @param moleculeID - the ID referencing a molecule
     * @param nextLine - the Line from were to start
     * @return a List of all Monomers, which could be found in the following lines.
     * @throws IOException if an error occurs while reading.
     */
    private List<Monomer> getAllMonomersFromChain(String moleculeID, String nextLine) throws IOException {
        List<Monomer> moleculesMonomers = null;
        Set<String> unknownMonoNames = new HashSet<>();
        if(nextLine != null && !nextLine.isBlank() && this.reader != null && moleculeID != null && !moleculeID.isBlank()) {
            moleculesMonomers = new ArrayList<>();
            //store all monomers of one molecule in a list and keep them mapped
            while (nextLine.substring(11, 12).equals(moleculeID)) {
                // all monomers belong to this molecule.
                String[] monomerNames = nextLine.substring(19).split(" ");

                //get monomer IDs of this line
                for (int i = 0; i < monomerNames.length; i++) {
                    //monomers can be separated by 1,2 or 3 spaces depending of the length of the name
                    // --> empty strings in monomers possible: trim
                    String monomerName = monomerNames[i].trim().toUpperCase();
                    if (!monomerName.isBlank()) {
                        AminoAcid_Nucleotide monomerType = AminoAcid_Nucleotide.get(monomerName);
                        //                                          only saves some replicated printings
                        if(monomerType.getName().equals("OTHER") && !unknownMonoNames.contains(monomerName)){
                            System.out.println("Sorry, there was an amino acid or nucleotide which is unknown to me: " + monomerName + ". Therefore it will not be parsed.");
                            unknownMonoNames.add(monomerName);
                            moleculesMonomers.add(new Monomer(i, monomerType));
                        }
                    }
                }
                nextLine = this.reader.readLine();
            }
        }
        return moleculesMonomers;
    }

    /**
     * Find the monomer which is matching the given parameters. If all PDB files were correct and consistent, than the according
     * Monomer should be found at position seqNumber. But sometimes this will not match a monomer with the given
     * monomer name/type.
     * Then the position of the first occurrence of this monomer name before seqNumber in the chain will be given
     * as it is probably the beginning of the polymer which was not mentioned in the SEQRES section.
     * In case there is no occurrence of the given name in the chain before seqNumber, the closest position after seqNumber will be given.
     * @param storedMonomerOfChain list of the Monomer which were already parsed to belong to the according chain;
     *                             shall not be null and shall not have a size smaller or equal to zero.
     * @param monomerName the name of the monomer which can be an amino acid or nucleotide;
     *                    shall not be null or blank
     * @param seqNumber number identifying the position of this monomer within the according chain;
     *                        must be greater than zero.
     * @return the position of the monomer in the list of the according chain if any was found,
     *         a negative value otherwise or when any of the given parameter was not valid.
     */
    private int findMatchingMonomerPosition(List<Monomer> storedMonomerOfChain, String monomerName, int seqNumber) {
        if(storedMonomerOfChain != null && storedMonomerOfChain.size() > 0 && monomerName != null && !monomerName.isBlank() && seqNumber > 0) {
            monomerName = monomerName.trim().toUpperCase();

            if (seqNumber < storedMonomerOfChain.size()) {
                if (storedMonomerOfChain.get(seqNumber).getType().name().equals(monomerName)) {
                    return seqNumber; //file is valid.
                }
            }
            else {
                //some monomers were not mentioned in file.
                for (int j = 0; j < storedMonomerOfChain.size(); j++) {
                    if (storedMonomerOfChain.get(j).getType().name().equals(monomerName)) {
                        return j;
                    }
                }
            }
            //Monomer is not contained in the given list, but maybe in other structure
        }
        return -1;
    }

    /**
     * Find the position of the chain with ID chainID in the local list. This method can be used to later save time looping through chains.
     * @param chainID ID of the chain to be found
     * @return the position of the chain with searched chainID if present, a negative value otherwise.
     */
    private int findChainPosition(String chainID) {
        if(chainID != null && !chainID.isBlank()) {
            chainID = chainID.trim().toUpperCase();
            for (int j = 0; j<this.chains.size(); j++) {
                if (this.chains.get(j).getUnitID().equals(chainID)) {
                    return j;
                }
            }
            System.err.println("Searching for Chain " + chainID + " failed.");
        }
        return -1;
    }


    /**
     * Checks if filename with provided name was already created on default location with default file extension
     * @param filename of the file to check for existence, without file extension
     *                 not null or blank
     * @return true if the file is already present in the default location, false otherwise or if filename is null or blank
     */
    public static boolean isFileCreated(String filename){
        if(filename != null && !filename.isBlank()) {
            File pdbFile = new File(FileParser.DEFAULT_STORING_DIRECTORY + filename + FileParser.FILE_EXTENSION);
            return pdbFile.exists();
        }
        return false;
    }

}
