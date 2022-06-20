package model;

/**
 * The FourMer class represents a 4-mer of a DNA sequence. It contains the actual 4-mer and it's hash value.
 * Like DNA sequences, also 4-mer only contain nucleotides A, C, G and T.
 * For a 4-mer M = x0x1x2x3 the hash value is computed according to the following equation: hash = x0*64 + x1*16 + x2*4 + x3.
 */
public class FourMer implements Comparable{

    private final String kmer;
    private final int hash;

    public FourMer(String kmer) throws InvalidKmerException {
        this.kmer = getIfValidKmer(kmer);
        this.hash = calcHash();
    }


    /**
     * Calculates the hash code of this object.
     *
     * @return the hash code of kmer as an integer value
     * -1 if kmer is null or has a length != 4
     */
    private int calcHash() {
        int hash = 0;
            for (int i = 0; i < 4; i++) {
                // get value according to nucleotides in the 4mer
                switch (kmer.charAt(i)) {
                    case 'A':
                        // has value 0 and therefore does not change the hash value.
                        break;
                    case 'T':
                        hash += (1 * (Math.pow(4, (3 - i))));
                        break;
                    case 'C':
                        hash += (2 * (Math.pow(4, (3 - i))));
                        break;
                    case 'G':
                        hash += (3 * (Math.pow(4, (3 - i))));
                        break;
                    default:
                        //in case another letter occurs --> ignore the the complete 4mer
                        return -1;
                }
            }
        return hash;
    }

    //Getter and Setter:
    public String getKmer() {
        return kmer;
    }

    /**
     * Sets the kmer of this Object to the parameter kmer if it is valid.
     *
     * @param kmer - a string of length k containing k of the valid nucleotides specified in @see Model
     * @throws InvalidKmerException when kmer is an invalid String, which means either null, blanck or containing characters other than A, C, G or T.
     */
    public String getIfValidKmer(String kmer) throws InvalidKmerException {
        if (kmer != null && !kmer.isBlank()) {
            if(kmer.matches("[ATGC]{4}")){
                return kmer;
            }
            else{
                throw new InvalidKmerException("K-mer are not supposed to contain characters, which are non-nucleotides. Please, only use nucleotides: A,C,G or T.");
            }
        }
        else{
            throw new InvalidKmerException("K-mer are not supposed to be null or blank. Please, only use dna sequences containing A, C, G or T as nucleotides.");
        }
    }

    public int getHash() {
        //to prevent inconsistency (kmer could be changed and one forgot to recalculate the hash value, so it would be from the old kmer)
        //the hash should be recalculated before returning.
        calcHash();
        return hash;
    }

    @Override
    public int compareTo(Object o) {
        if(o.getClass() == this.getClass()){
            int thisHashCode = getHash();
            int otherHashCode = ((FourMer) o).getHash();
            return Integer.compare(thisHashCode, otherHashCode);
        }
        else{
            System.err.println("FourMer class can not be compared to a class of type: " + o.getClass());
            return -4;
        }
    }
}
