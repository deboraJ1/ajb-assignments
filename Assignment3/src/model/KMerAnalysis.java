package model;

import java.util.HashSet;
import java.util.Set;

/**
 * KMerAnalysis class analyses a set of given DNA sequences. Distances between kmers of length k can be computed between all sequences.
 */
public class KMerAnalysis {

    /**
     * Compute the distances for all pairs of sequences.
     * @param sequences - Sequences for which the distances are computed pairwise
     * @param kMerSize - length of kmer for which the distances are computed
     * @return
     */
    public static double[][] computeDistances (Sequences sequences, int kMerSize) {
        double[][] distanceMatrix = new double[sequences.size()][sequences.size()];
        for(int i = 0; i < distanceMatrix.length; i++) {
            for(int j = i+1; j < distanceMatrix.length;j++)
                distanceMatrix[i][j] = distanceMatrix[j][i] = computeDistance(kMerSize, sequences.get(i).sequence(), sequences.get(j).sequence());
        }
        return distanceMatrix;
    }

    /**
     * Compute the distance between two sequences given kmers of size kmerSize.
     * @param kmerSize - the size of the kmers for which the distance will be computed
     * @param sequence1 - first sequence
     * @param sequence2 - second sequence
     * @return the distance between first and second sequence.
     *          0 -  if any of the following conditions is true for at least one sequence
     *              a) sequence is blank
     *              b) sequence is null
     *              c) sequence does not contain any full kmer of size kmerSize
     */
    private static double computeDistance(int kmerSize, String sequence1, String sequence2) {
        if (sequence1 != null && !sequence1.isBlank() && sequence2 != null && !sequence2.isBlank()) {
            //set of all kmers for each sequence
            Set<String> kmers1 = new HashSet<String>();
            Set<String> kmers2 = new HashSet<String>();

            for (int i = 0; i <= sequence1.length() - kmerSize; i++) {
                //add all complete kmers of size kmerSize to sequence1
                kmers1.add(sequence1.substring(i, i + kmerSize));
            }
            for (int i = 0; i <= sequence2.length() - kmerSize; i++) {
                //add all complete kmers of size kmerSize to sequence2
                kmers2.add(sequence2.substring(i, i + kmerSize));
            }

            if (kmers1.isEmpty() || kmers2.isEmpty())
                return 0;

            //filter all kmers which are in both sets (kmers1 and kmers2)
            // and count
            long intersectionSize = kmers1.stream().filter(kmers2::contains).count();
            if (intersectionSize == 0)
                return 1;
            //distance = log of (intersection / minimum of both set sizes)
            return -Math.log((double) intersectionSize / Math.min(kmers1.size(), kmers2.size()));
        }
        else{
            return 0;
        }
    }
}