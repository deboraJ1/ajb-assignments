package model.visitor;
import model.InvalidValueException;
import model.TreeNode;

/**
 * TreeVisitorI is an interface realizing the visitor pattern and
 * can be implemented when a phylogenetic tree shall be visited for different computations
 */
public interface TreeVisitorI {

    public void visit(TreeNode node) throws InvalidValueException;

}
