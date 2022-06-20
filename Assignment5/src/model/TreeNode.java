package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class TreeNode represents a single node within a phylogenetic tree.
 * A Node can have several children (if it is representing an inner node in the tree) or no children (if it is a leave node).
 * Only the root node of a tree is supposed to have no parent, all other nodes in a tree have exactly one parent, which is also an Object of TreeNode.
 * All nodes have a label and a distance associated. The distance defines the distance to the parent.
 */
public class TreeNode {

    private TreeNode parent;            //the parent of this node, is only null if this node is a root node.
    private List<TreeNode> children;    //list can be empty, if this is a leave node
    private String label;
    private double distance;            //distance to parent node


    public TreeNode(){
        this.children = new ArrayList<>();
        this.label = "";
        this.distance = 0;
    }


    public TreeNode getParent() {
        return parent;
    }

    /**
     * sets the parent of this TreeNode.
     * @param parent - parent of this TreeNode.
     *               can be null if this TreeNode is the root Node of the according tree.
     */
    public void setParent(TreeNode parent) {
        this.parent = parent;
    }

    public List<TreeNode> getChildren() {
        return children;
    }

    /**
     * Set the all children of this TreeNode to the given value
     * @param children - list of children to be set as children of this TreeNode.
     *                 can be empty, if this node is a leave node.
     * @throws InvalidValueException if children is null.
     */
    public void setChildren(List<TreeNode> children) throws InvalidValueException {
        if(children != null){
            this.children = children;
        }
        else{
            throw new InvalidValueException("Children of a TreeNode can be a list of size 0 but can not be null.");
        }
    }

    public String getLabel() {
        return label;
    }

    /**
     * sets the label of this TreeNode
     * @param label - the label to be set.
     * @throws InvalidValueException - if label is null
     */
    public void setLabel(String label) throws InvalidValueException{
        if(label == null){
            throw new InvalidValueException("A label of a TreeNode can not be null.");
        }
        this.label = label;
    }

    public double getDistance() {
        return distance;
    }

    /**
     * Set the distance of this node to its parent node
     * @param distance - to the parent node, is not supposed to be smaller than 0. Can be 0, e.g. for the root node.
     * @throws InvalidValueException - if a distance smaller than 0 is tried to be set.
     */
    public void setDistance(double distance) throws InvalidValueException{
        if(distance < 0){
            throw new InvalidValueException("Distance of a TreeNode can not be smaller than 0");
        }
        this.distance = distance;
    }
}
