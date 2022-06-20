package model;

import java.util.Stack;

/**
 * read and write phylogenetic tree in Newick format
 * Daniel Huson, 5.2021
 */
public class NewickIO {

    /**
     * parses a Newick string
     * @param newick description of rooted phylogenetic tree in Newick format
     * @return root node
     * @throws InvalidValueException if label is null or has a size <= 0 or if node is null
     */
    public static TreeNode valueOf(String newick) throws InvalidValueException {
        if (newick != null && !newick.isBlank()) {
            if (newick.endsWith(";")) {
                newick = newick.substring(0, newick.length() - 1);
            }
            //otherwise no substring needs to be created

            final var stack = new Stack<TreeNode>();
            final var reverseLabel = new StringBuilder();
            TreeNode current = null;
            TreeNode root = null;

            loop:
            for (int i = newick.length() - 1; i >= 0; i--) {
                char ch = newick.charAt(i);
                switch (ch) {
                    case ')' -> {
                        var node = new TreeNode();
                        if (current != null) {
                            stack.push(current);
                            current.getChildren().add(0, node);
                            node.setParent(current);
                        } else {
                            root = node;
                        }
                        parseLabel(reverseLabel.reverse().toString(), node);
                        current = node;
                        reverseLabel.setLength(0);
                    }
                    case '(' -> {
                        if (reverseLabel.length() > 0) {
                            var node = new TreeNode();
                            if (current != null) {
                                current.getChildren().add(0, node);
                            }
                            node.setParent(current);
                            parseLabel(reverseLabel.reverse().toString(), node);
                        }
                        if (stack.size() > 0)
                            current = stack.pop();
                        else
                            break loop;
                        reverseLabel.setLength(0);
                    }
                    case ',' -> {
                        if (reverseLabel.length() > 0) {
                            var node = new TreeNode();
                            if (current != null)
                                current.getChildren().add(0, node);
                            node.setParent(current);
                            parseLabel(reverseLabel.reverse().toString(), node);
                        }
                        reverseLabel.setLength(0);
                    }
                    default -> reverseLabel.append(ch);
                }
            }
            return root;
        }
        else{
            throw new InvalidValueException("The given String was null or blanck, which can not be a valid newick format to be parsed in a Tree.");
        }
    }

    /**
     * Parses a Label given as String in Newick Format to a TreeNode.
     * @param label - the label to be parsed
     * @param node - to be created from label
     * @throws InvalidValueException if label is null or has a size <= 0 or if node is null
     */
    private static void parseLabel(String label, TreeNode node) throws InvalidValueException{
        if (node != null && label != null) {
            int lastPos = label.lastIndexOf(":");
            if (lastPos >= 0) {
                String left = label.substring(0, lastPos).trim();
                if (left.length() > 0)
                    node.setLabel(left);
                String right = label.substring(lastPos + 1).trim();
                if (right.length() > 0)
                    node.setDistance(Double.parseDouble(right));
            } else if (node.getParent() != null)
                node.setDistance(1);
            else
                node.setDistance(0);
        }
        else{
            throw new InvalidValueException("Parsing a Label to a node is only possible if none of label and node is null.");
        }
    }

    /**
     * Returns the Newick string for a rooted phylogenetic tree
     * @param root the root node
     * @param includeDistances whether to write distances
     * @return string representation in Newick format
     */
    public static String toString(TreeNode root, boolean includeDistances) {
        var buf = new StringBuilder();
        toStringRecursive(root,includeDistances, buf);

        buf.append(";");
        return buf.toString();
    }

    /**
     * Builds a String in Newick Format from a given TreeNode v and all its children.
     * Distances might be included or not depending on the parameter includeDistances.
     * @param v - the root to be represented in Newick Format as well as its children
     * @param includeDistances - true if distances shall be included in the resulting String
     * @param buf - the StringBuilder which is used to create the NewickFormat of v and its children.
     *              might also contain an error message if v is null.
     */
    private static void toStringRecursive(TreeNode v, boolean includeDistances, StringBuilder buf) {
        if (v != null) {
            if(v.getChildren().size() > 0){
                buf.append("(");
                boolean first = true;
                for (var w : v.getChildren()) {
                    if (first) {
                        first = false;
                    } else {
                        buf.append(",");
                    }
                    toStringRecursive(w, includeDistances, buf);
                }
                buf.append(")");
            }
            if(v.getLabel()!=null) {
                buf.append(v.getLabel());
            }
            if(includeDistances && v.getParent()!=null) {
                buf.append(":").append(v.getDistance());
            }
        }
        else{
            buf.append("No String can be computed from a TreeNode v and its children if v is null.");
        }
    }
}

