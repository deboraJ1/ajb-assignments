package model;

import java.util.*;
import controller.Controller;

/**
 * Model.Model class is one part to realize the Model.Model-View-Controller.Controller principle.
 * The model contains data, logic and rules of the application. The input from the @see View is transformed
 * into valid commands for this class from @see Controller.Controller. @see View is then updated from this class.
 */
public class Model {

    public static final List<Character> validNucleotides = generateValidNucleotides();
    private Controller controller;

    private TreeMap<FourMer, Integer> duplicateCounts;   //values are the number of occurrence of this 4mer in the dna sequence

    public Model(Controller controller) {
        if (controller != null) {
            this.controller = controller;
        } else {
            System.err.println("Controller can not be null!");
        }
    }

    private static List<Character> generateValidNucleotides() {
        Character[] nucleotides = {'A', 'C', 'G', 'T'};
        return new ArrayList<Character>(Arrays.asList(nucleotides));
    }

    /**
     * alculates all consecutive 4 mers of a given dna sequence. All 4 mers are stored in an array and returned at the end.
     * The last kmer in the array might not have the full length of 4.
     * If the dna sequence is shorter than 4, then the array will only contain one element which is shorter than 4.
     *
     * @param dnaSequence - the sequence from which the 4 mer are computed
     * @return an array containing all consecutive 4mers in the sequence
     * @throws InvalidKmerException when the dnaSequence contains any invalid character, which is not a nucleotide or when dnaSequence is
     */
    public FourMer[] calculate4Mers(String dnaSequence) throws InvalidKmerException {
        if (dnaSequence != null && !dnaSequence.isBlank()) {
            dnaSequence = dnaSequence.toUpperCase();                   //ensure, that only uppercase nucleotides are used.
            int fourMerNo = Math.toIntExact(dnaSequence.length() / 4); //number of 4mers in the dna sequence
            FourMer[] fourMers = new FourMer[fourMerNo];               //array storing the 4mers

            //fill 4mer array:
            if (dnaSequence.length() > 4) {
                //add all 4mers to the array
                //if the last 4mer is not complete, it will not be added because of the way fourMerNo is computed above.
                for (int i = 0; i < fourMerNo; i++) {
                    int startIndex = i * 4;
                    FourMer nextFourMer = new FourMer(dnaSequence.substring(startIndex, startIndex + 4));
                    fourMers[i] = nextFourMer;
                }
            } else {
                // in case the sequence is shorter than 4, the following is more efficient
                fourMers[0] = new FourMer(dnaSequence);
            }
            return fourMers;
        }
        else{
            throw new InvalidKmerException("DNA sequence is not supposed to be null or blanck.");
        }
    }

    /**
     * Counts how often each of the given fourMers occurs
     * @param fourMers - contains all 4 mer that have been computed for a given DNA sequence.
     * @return a TreeMap where the keys are the 4 mers and the values are the according count of occurrences.
     *         Null if fourMers is null or fourMers does not contain any elements.
     */
    public TreeMap<FourMer, Integer> countOccurrenceOfFourMers(FourMer[] fourMers) {
        int fourMerNo = fourMers.length;

        if (fourMers != null && fourMerNo > 0) {
            this.duplicateCounts = new TreeMap<FourMer, Integer>();

            for (int i = 0; i < fourMerNo; i++) {
                // count occurrence of all 4mers
                FourMer key = fourMers[i];
                Integer keyCount = duplicateCounts.put(key, 1);

                //when key is already key in duplicateCounts, increment the count by one, otherwise set to one.
                keyCount = (keyCount != null ? keyCount += 1 : 1);
                this.duplicateCounts.put(key, keyCount);
            }
        }
        else {
            this.duplicateCounts = null;
        }
        return this.duplicateCounts;
    }
}
