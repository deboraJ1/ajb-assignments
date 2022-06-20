package model.molecules;

public enum AminoAcid_Nucleotide {
    //amino acids
    ALA("ALA"),
    ARG("ARG"),
    ASN("ASN"),
    ASP("ASP"),
    CYS("CYS"),
    GLN("GLN"),
    GLU("GLU"),
    GLY("GLY"),
    HIS("HIS"),
    ILE("ILE"),
    LEU("LEU"),
    LYS("LYS"),
    MET("MET"),
    PHE("PHE"),
    PRO("PRO"),
    SER("SER"),
    THR("THR"),
    TRP("TRP"),
    TYR("TYR"),
    VAL("VAL"),
    //nucleotides
    G("G"),
    A("A"),
    C("C"),
    T("T"),
    U("U"),
    DA("DA"),
    DC("DC"),
    DG("DG"),
    DT("DT"),
    DI("DI"),
    OTHER("OTHER");

    private final String name;

    AminoAcid_Nucleotide(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    public static AminoAcid_Nucleotide get(String name) {
        for(AminoAcid_Nucleotide monomerName : values()) {
            if (monomerName.getName().equalsIgnoreCase(name)) {
                return monomerName;
            }
        }
        return OTHER;
    }

}
