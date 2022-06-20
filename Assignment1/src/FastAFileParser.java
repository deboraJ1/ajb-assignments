import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * A Reader especially for FastA files containing exactly one DNA sequence.
 * The fastAFileParser will
 *      (1) read the sequence from the file,
 *      (2) compute all 4mers from the sequence and
 *      (3) calculate their hash codes.
 * Hash codes will be printed out together with their 4mer and the amount of duplicated occurrences in the sequence.
 *
 * @author Debora Jutz
 */
public class FastAFileParser {

    private BufferedReader reader;

    private TreeMap<Integer, Set<String>> hashes;       // keys are the hash codes, values are the kmers, which are keys in duplicateCounts
    private HashMap<String, Integer> duplicateCounts;   //values are the number of occurrence of this 4mer in the dna sequence

    /**
     * Constructor of the class FastAFileParser.
     *
     * @param inputFile - path to the fastA file from which the dna sequence can be read for further parsing operations.
     */
    public FastAFileParser(String inputFile){
        if(inputFile != null && inputFile.length() > 0 ){
            try{
                this.reader = new BufferedReader(new FileReader(inputFile));
            }
            catch (FileNotFoundException e){
                System.out.println("The path to the fastA input file can not be found. Please specify a valid path.");
                return;
            }
        }
        else{
            System.out.println("Please specify a valid path to the fastA file you want to be processed. The file path is not supposed to be empty or null.");
            return;
        }
        String dnaSequence = extractDNA();
        String[] fourMers = calculate4Mers(dnaSequence);
        computeHashes(fourMers);
        printToTerminal(this.hashes, this.duplicateCounts);

    }

    /**
     * reads a fastA file and returns the DNA sequence given in the fastA file as a String in Uppercase letters.
     * The file is only supposed to contain one DNA sequence and is made known to the FastAFileReader when calling the constructor.
     * @return dna sequence in upper case letters, empty string if file does not contain any dna sequence
     */
    public String extractDNA() {
        String dna = "";
        boolean dna_seq_reading = false;
        try{
            //extract dna sequence: name of sequence starts with >, dna does not.
            String nextLine = reader.readLine();

            while (nextLine != null){
                if(nextLine.startsWith(">")){
                    nextLine = reader.readLine();
                    dna_seq_reading = false;
                }
                else{
                    if(dna_seq_reading){
                        dna +=nextLine;
                    }
                    else{
                        dna = nextLine;
                    }
                    nextLine = reader.readLine();
                    dna_seq_reading = true;
                }
            }
        }
        catch (IOException e){
            System.out.println("Sorry a problem occurred while reading the file.");
        }
        dna.toUpperCase();
        return dna;
    }

    /**
     * Calculates all consecutive 4 mers of a given dna sequence. All 4 mers are stored in an array and returned at the end.
     * The last kmer in the array might not have the full length of 4.
     * If the dna sequence is shorter than 4, then the array will only contain one element which is shorter than 4.
     * @param dnaSequence - the sequence from which the 4 mer are computed
     * @return an array containing all consecutive 4mers in the sequence
     */
    public String[] calculate4Mers(String dnaSequence) {
        //number of 4mers in the dna sequence
        int fourMerNo = Math.toIntExact(dnaSequence.length() / 4);

        String[] fourMers = new String[fourMerNo];                              //array storing the 4mers

        //fill 4mer array:
        if (dnaSequence != null && dnaSequence.length() > 4){
            //add all 4mers to the array
            //if the last 4mer is not complete, it will not be added because of the way fourMerNo is computed above.
            for (int i = 0; i < fourMerNo; i++) {
                int startIndex = i * 4;
                fourMers[i] = dnaSequence.substring(startIndex, startIndex + 4);
            }
        }
        else{
            // in case the sequence is shorter than 4, the following is more efficient
            fourMers[0] = dnaSequence;
        }
        return fourMers;
    }

    /**
     * compute hash code for each 4 mer of fourMers
     * @param fourMers - contains all 4 mer for which a hash code shall be computed
     * The following will be stored:
     * One Map holding all hash codes associated with a Set of 4mers, for which this hash code was computed:
     *                      the key as String represents the hash code
     *                      the value as Set<String> stores all 4 mers, for which key was computed as hash code.
     * A second Map keeps track of how often each 4mer is found in the dna sequence.
     *
     * Both Maps are null if fourMers is null or has a size < 1.
     */
    public void computeHashes(String[] fourMers){
        int fourMerNo = fourMers.length;

        if (fourMers != null && fourMerNo > 0) {
            //init
            this.hashes = new TreeMap<>();
            this.duplicateCounts = new HashMap<>();

            for (int i = 0; i < fourMerNo; i++) {

                // calculate hashes of all 4mers
                String value = fourMers[i];
                Integer key = Integer.valueOf(calcHash(value));

                // store all 4mers with same hash value in one set of values.
                if (this.hashes.containsKey(key)) {
                    Set<String> fourMersWithSameHash = this.hashes.get(key);
                    //increase number of duplicates if value was already present in set
                    if(!fourMersWithSameHash.add(value)){
                        this.duplicateCounts.put(value, this.duplicateCounts.get(value)+1);
                    }
                    this.hashes.put(key, fourMersWithSameHash);
                }
                else {
                    Set<String> fourMerOfHash = new HashSet<>();
                    fourMerOfHash.add(value);
                    this.duplicateCounts.put(value, 1);
                    this.hashes.put(key, fourMerOfHash);
                }
            }
        }
        else{
            this.hashes = null;
            this.duplicateCounts = null;
        }
    }

    /**
     * Calculates the hash code of a given 4 mer.
     * @param kmer - a 4 mer (has to have an exact length of 4)
     * @return the hash code of kmer as an integer value
     *         -1 if kmer is null or has a length != 4
     */
    private int calcHash(String kmer){
        int hash = 0;
        if (kmer != null && kmer.length() == 4){
            
            for (int i = 0; i < 4; i++){
                // get value according to nucleotides in the 4mer
                int nucValue;
                switch (kmer.charAt(i)){
                    case 'A':
                        // has value 0 and therefore does not change the hash value.
                        break;
                    case 'T':
                        hash += (1 * (Math.pow(4, (3-i))));
                        break;
                    case 'C':
                        hash += (2 * (Math.pow(4, (3-i))));
                        break;
                    case 'G':
                        hash += (3 * (Math.pow(4, (3-i))));
                        break;
                    default:
                        //in case another letter occurs --> ignore the the complete 4mer
                        return -1;
                }
            }
        }
        else{
            return -1;
        }
        return hash;
    }

    /**
     * Prints the result to the Terminal.
     * @param hashes - all hash codes associated with a Set of 4mers, for which the key (hash code) was computed
     * @param duplicateCounts - associates each 4mer with a number of how often it was found in the dna sequence.
     */
    private void printToTerminal(TreeMap<Integer, Set<String>> hashes, HashMap<String, Integer> duplicateCounts){
        if(hashes != null && duplicateCounts != null){

            StringBuilder sb = new StringBuilder();
            Formatter formatter = new Formatter();

            Set<Integer> hashValues = hashes.keySet();
            for (Integer key : hashValues) {

                Set<String> kmerSet = hashes.get(key);
                for (String kmer : kmerSet){
                    int numberOfDuplication = duplicateCounts.get(kmer);
                    formatter = formatter.format(Locale.GERMAN, "%1$3s   %2$4s   %3$3x %n", key, kmer, numberOfDuplication);
                }
            }
            System.out.println(formatter);
        }
    }

}
