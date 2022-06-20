package model;

/**
 * This is an interface for a Tree which shall be visible by e.g. a TreeVisitorI.
 * Being visibla offers several methods of calculation:
 * total edge length of a tree, number of nodes and number of leave nodes as well as check if a tree is non-binary or binary.
 */
public interface VisableTreeI {
    //All those methods are commonly known to be called accept(...) but other names where chosen for better understanding.

    public double getTotalLength() throws InvalidValueException;

    public int getNodeNumber() throws InvalidValueException;

    public int getLeaveNodeNumber() throws InvalidValueException;

    public boolean isBinary() throws InvalidValueException;
}
