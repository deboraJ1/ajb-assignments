package model.visitor;

import model.InvalidValueException;
import model.TreeNode;

import java.util.concurrent.atomic.LongAdder;

public class NodeNumberTreeVisitor implements TreeVisitorI{

    private LongAdder nodeAdder;

    public NodeNumberTreeVisitor(){
        this.nodeAdder = new LongAdder();
    }

    @Override
    public void visit(TreeNode node) throws InvalidValueException {
        if(node != null) {
            this.nodeAdder.add(node.getChildren().size());
        }
        else{
            throw new InvalidValueException("Number of nodes can not be calculated because at least one node was found to be null.");
        }
    }

    public int getNodeNumber(){
        return this.nodeAdder.intValue();
    }
}
