package model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.DoubleAdder;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.Consumer;

/**
 * Class PhyloTree represents a rooted phylogenetic tree and can be binary or non-binary.
 */
public class PhyloTree {

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

    /**
     * get the root node of this tree.
     * @return
     */
    public TreeNode getRoot() {
        return root;
    }

    /**
     * Method to visit all nodes below 'node' in a recursive way.
     * To visit all nodes of this phylogenetic tree, call this method with node being the root node of the tree.
     * @param node - the node to start visiting.
     * @param visitor - operation to be performed on all nodes which will be visited.
     */
    public void preOrderTraversal(TreeNode node, Consumer<TreeNode> visitor)  {
        visitor.accept(node);
        for (TreeNode child : node.getChildren()){
            preOrderTraversal(child, visitor);
        }
    }

    /**
     * Get the total length of this phylogenetic tree, meaning the sum of the length of all edges.
     * @return the total length, never < 0.
     */
    public double getTotalLength() {
        DoubleAdder totalLength = new DoubleAdder();
        preOrderTraversal(getRoot(), node -> totalLength.add(node.getDistance()));
        return totalLength.sum();
    }

    /**
     * Get the number of nodes in this phylogenetic tree.
     * @return the number of nodes in this tree (including all nodes: root, inner nodes and leave nodes)
     */
    public int getNodeNumber() {
        LongAdder nodeNumber = new LongAdder();
        preOrderTraversal(getRoot(), node -> nodeNumber.add(node.getChildren().size()));
        //add one because root node is never added (only adds children)
        return nodeNumber.intValue() + 1;
    }

    /**
     * get the number of leave nodes in this tree
     * @return the number of nodes in this tree, which do not have any children. Is never < 0.
     *          Could be 0 if only one node were in the tree.
     */
    public int getLeaveNodeNumber() {
        LongAdder leaveNodes = new LongAdder();
        preOrderTraversal(getRoot(), node -> {
            if(node != null){
                if(node.getChildren().size() == 0) {
                    leaveNodes.increment();
                }
                //else: not a leave node
            }
            else{
                System.err.println("Leave nodes can not be counted in a tree containing nodes, which are null.");
            }
        });
        return leaveNodes.intValue();
    }

    /**
     * Checks whether this object of PhyloTree is a binary phylogenetic tree or not.
     * @return - true if the tree is binary (every node has exactly 2 children except of the leave nodes)
     */
    public boolean isBinary() {
        AtomicBoolean isBinary = new AtomicBoolean(true);
        preOrderTraversal(getRoot(), node -> {if(node.getChildren().size() == 1 || node.getChildren().size() > 2){
            isBinary.set(false);
        }});
        return isBinary.get();
    }

}
