package model.molecules;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class Structure {

    private final String id;
    private final StructureType type;
    private List<Monomer> monomers;

    public Structure(String structureID, StructureType type){
        if(structureID != null && !structureID.isBlank()){
            this.id = structureID.trim().toUpperCase();
        }
        else{
            throw new InvalidParameterException("Structure with structure ID, which is null or empty can not be created.");
        }
        if(type != null){
            this.type = type;
        }
        else{
            this.type = StructureType.OTHER;
        }
        this.monomers = new ArrayList<>();
    }

    public String getId(){
        return this.id;
    }

    public StructureType getStructureType(){
        return this.type;
    }
    public List<Monomer> getMonomers(){
        return this.monomers;
    }

    public void setMonomers(List<Monomer> monomers){
        if(monomers != null){
            this.monomers = monomers;
        }
    }

}
