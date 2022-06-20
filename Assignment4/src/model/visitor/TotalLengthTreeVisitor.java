package model.visitor;

import model.InvalidValueException;
import model.TreeNode;

import java.util.concurrent.atomic.DoubleAdder;

public class TotalLengthTreeVisitor implements TreeVisitorI{

    private DoubleAdder lenghtAdder;

    public TotalLengthTreeVisitor(){
        this.lenghtAdder = new DoubleAdder();
    }

    @Override
    public void visit(TreeNode node) throws InvalidValueException {
        if(node != null && node.getDistance() >= 0) {
            this.lenghtAdder.add(node.getDistance());
        }
        else{
            throw new InvalidValueException("Total length can not be calculated with null nodes.");
        }
    }

    public double getTotalLength(){
        return this.lenghtAdder.sum();
    }

}
