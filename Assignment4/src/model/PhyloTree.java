package model;


import model.visitor.*;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/**
 * Class PhyloTree represents a rooted phylogenetic tree and can be binary or non-binary.
 */
public class PhyloTree implements VisableTreeI{

    private TreeNode root;
    private boolean isBinary;
    private int edges, nodes, leaveNodes;
    private double totalEdgeLength;

    /**
     * Constructor of the class PhyloTree.
     * @param root - the root Node of this phylogenetic tree.
     * @throws InvalidValueException - if root is null
     */
    public PhyloTree(TreeNode root) throws InvalidValueException{
        if(root == null){
            throw new InvalidValueException("No PhyloTree can be created from a root node which is null.");
        }
        this.root = root; //tree can then be read from root node if needed.
        this.isBinary = true;
    }

    /**
     * Constructor of the class PhyloTree.
     * @param newickRepresentation - the phylogenetic tree in newick format
     * @throws InvalidValueException - if newickRepresentation is null or blanck
     */
    public PhyloTree(String newickRepresentation) throws InvalidValueException {
         TreeNode root = writeTree(newickRepresentation); //if newickRepresentation is null or blanck, exception is thrown
         this.root = root;
         this.isBinary = true;
    }

    /**
     * Read this tree and produce a string representation of it.
     * @param includeDistances - true if distances shall be included in the resulting newick format of this tree.
     * @return the Newick format of this tree.
     */
    public String readTree(boolean includeDistances){
        //nothing to check: root is only set to valid values or exception was already thrown.
        return NewickIO.toString(this.root, includeDistances);
    }

    /**
     * Write this tree from NewickFormat
     * @param newickTree - the newick representation of the Tree to be written.
     * @return the root node of this tree;
     * @throws InvalidValueException - if newickTree is null or blanck
     */
    public TreeNode writeTree(String newickTree) throws InvalidValueException{
        return NewickIO.valueOf(newickTree); //if newickTree is null or blanck, exception is thrown
    }

    public TreeNode getRoot() {
        return root;
    }

    public void preOrderTraversal(@NotNull TreeVisitorI visitor, TreeNode node) throws InvalidValueException {
        visitor.visit(node);
        for (TreeNode child : node.getChildren()){
            preOrderTraversal(visitor, child);
        }
    }

    //Methods to implement from VisableTreeI, more often those methods are called accept but this seems to be confusing here
    @Override
    public double getTotalLength() throws InvalidValueException {
        TotalLengthTreeVisitor totalLength = new TotalLengthTreeVisitor();
        preOrderTraversal(totalLength, getRoot());
        return totalLength.getTotalLength();
    }

    @Override
    public int getNodeNumber() throws InvalidValueException {
        NodeNumberTreeVisitor nodeNumber = new NodeNumberTreeVisitor();
        preOrderTraversal(nodeNumber, getRoot());
        //add one because root node is never added (only adds children)
        return nodeNumber.getNodeNumber()+1;
    }

    @Override
    public int getLeaveNodeNumber() throws InvalidValueException  {
        LeaveNodeNoTreeVisitor leaveNodes = new LeaveNodeNoTreeVisitor();
        preOrderTraversal(leaveNodes, getRoot());
        return leaveNodes.getLeaveNodeNumber();
    }

    @Override
    public boolean isBinary() throws InvalidValueException {
        BinaryTreeVisitor isBinaryTree = new BinaryTreeVisitor();
        preOrderTraversal(isBinaryTree, getRoot());
        return isBinaryTree.isBinary();
    }

}
