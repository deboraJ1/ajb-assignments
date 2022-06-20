package model;
import javafx.geometry.Point2D;

import java.util.HashMap;
import java.util.Map;

/**
 * computes "left-to-right" embedding for rooted tree
 * Daniel Huson, 5.2021
 */
public class TreeEmbedder {
    /**
     * computes an embedding of a rooted phylogenetic tree
     * @param tree the tree
     * @param toScale if true, all edges are draw to scale
     * @param width bounding box width
     * @param height bounding box height
     * @return mapping of nodes to coordinates
     */
    public static Map<TreeNode, Point2D> computeEmbedding(PhyloTree tree, boolean toScale, double width,double height) {
        var embedding=new HashMap<TreeNode, Point2D>();
        computeEmbeddingRec(tree.getRoot(),toScale?0:-maxDistToLeaf(tree.getRoot(),false),0,toScale,embedding);
        fit(embedding,width,height);
        return embedding;
    }

     private static int computeEmbeddingRec(TreeNode v, double hDistToRoot, int leafNumber, boolean toScale, Map<TreeNode,Point2D> embedding) {
        final double x;
        final double y;
        if(v.getChildren().size()==0) {
            if(toScale)
                x=hDistToRoot;
            else
                x=0;
            leafNumber++;
            y=leafNumber;
        }
        else {
            Point2D first=null;
            Point2D last=null;
            for(var w:v.getChildren()) {
                leafNumber=computeEmbeddingRec(w,hDistToRoot+(toScale?w.getDistance():1),leafNumber,toScale,embedding);
                if(first==null) {
                    first=embedding.get(w);
                }
                last=embedding.get(w);
            }

            assert(first!=null && last!=null); // can't happen

                x=hDistToRoot;
                y=0.5*(last.getY()+first.getY());

        }
        embedding.put(v,new Point2D(x,y));
        return leafNumber;

    }

    private static double maxDistToLeaf(TreeNode v,boolean toScale) {
        var maxDist=0.0;
        for(var w:v.getChildren()) {
            maxDist=Math.max(maxDist,maxDistToLeaf(w,toScale));
        }
        return maxDist+(toScale?v.getDistance():1);
    }

    private static void fit(HashMap<TreeNode, Point2D> embedding, double width, double height) {
        var xMin= embedding.values().stream().mapToDouble(Point2D::getX).min().orElse(0);
        var xMax=embedding.values().stream().mapToDouble(Point2D::getX).max().orElse(0);
        var xFactor=(xMax>xMin)?width/(xMax-xMin):0;
        var yMax=embedding.values().stream().mapToDouble(Point2D::getY).max().orElse(0);
        var yMin= embedding.values().stream().mapToDouble(Point2D::getY).min().orElse(0);
        var yFactor=(yMax>yMin)?height/(yMax-yMin):0;

        for(var entry:embedding.entrySet()) {
            entry.setValue(new Point2D((entry.getValue().getX()-xMin)*xFactor,(entry.getValue().getY()-yMin)*yFactor));
        }
    }
}

