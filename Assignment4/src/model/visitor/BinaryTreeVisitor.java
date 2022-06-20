package model.visitor;

import model.InvalidValueException;
import model.TreeNode;

import java.util.concurrent.atomic.LongAdder;

public class BinaryTreeVisitor implements TreeVisitorI{

    private boolean isBinary;

    public BinaryTreeVisitor(){
        this.isBinary = true;
    }

    @Override
    public void visit(TreeNode node) throws InvalidValueException {
        if(node != null){
            int childrenSize = node.getChildren().size();
            if(childrenSize == 1 || childrenSize > 2) {
                //childrenSize of 0 is also accepted for leave nodes.
                this.isBinary = false;
            }
            //else: the tree in which node is inside is still binary.
        }
        else{
            throw new InvalidValueException("Binarity of a tree can not be calculated in a tree containing nodes, which are null.");
        }
    }

    public boolean isBinary() {
        return isBinary;
    }
}
