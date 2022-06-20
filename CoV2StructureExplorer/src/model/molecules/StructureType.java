package model.molecules;

/**
 * This enumeration provides different types of structure for Polymers.
 * HELIX - this polymer is an alpha helix in its secondary structure
 * SHEET - this polymer is an beta sheet in its secondary structure
 * NUCLEOTIDE - this polymer is neither HELIX nor SHEET
 * OTHER
 * INIT - used to show that initialization is not yet completed.
 */
public enum StructureType {
    HELIX, SHEET, NUCLEOTIDE, OTHER
}
