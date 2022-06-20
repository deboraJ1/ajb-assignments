package model;

/**
 * This class extends @see Exception and is thrown when an invalid kmer is found.
 * Invalid kmer are kmer, which are null, blanck or contain any nucleotide which is not valid.
 * Only nucleotides A, C, G and T are valid nucleotides. Any other character is not valid to be found within a kmer.
 */
public class InvalidKmerException extends Exception {


    public InvalidKmerException() {
        super("An invalid kmer was found. ");
    }

    public InvalidKmerException(String message) {
        super(message);
    }

}
