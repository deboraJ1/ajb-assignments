package model.visitor;

import model.InvalidValueException;
import model.TreeNode;

import java.util.concurrent.atomic.LongAdder;

public class LeaveNodeNoTreeVisitor implements TreeVisitorI{

    private LongAdder leaveNodeAdder;

    public LeaveNodeNoTreeVisitor(){
        this.leaveNodeAdder = new LongAdder();
    }

    @Override
    public void visit(TreeNode node) throws InvalidValueException {
        if(node != null){
            if(node.getChildren().size() == 0) {
                this.leaveNodeAdder.increment();
            }
            //else: not a leave node
        }
        else{
            throw new InvalidValueException("Leave nodes can not be counted in a tree containing nodes, which are null.");
        }
    }

    public int getLeaveNodeNumber(){
        return this.leaveNodeAdder.intValue();
    }
}
