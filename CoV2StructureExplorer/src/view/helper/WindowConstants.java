package view.helper;

import javafx.scene.paint.Color;

public class WindowConstants {

    private static final String about = "@author Debora Jutz\n" +
            "This program is only running when there is an internet connection. If there is no internet connection, this program will show an error message, informing the user that the PDB server can not be reached.\n" +
            "The Window will still appear, but the PDB entry IDs will not be loaded.\n" +
            "Otherwise the program supports loading PDB files from the server as well as storing them locally. The described models of a file can be read, parsed and shown.\n" +
            "Atoms are per default shown in their respective standard colors but the coloring can be changed to show different Structures, all helixes only, all sheets only, different molecules as well as in rainbow colors.\n" +
            "Molecules are per default shown in atom and stick view but it can be changed to ribbon view.\n" +
            "\n" +
            "Two kinds of animations are provided to help the user to get a better understanding of the program. These are:\n" +
            " 1. Explosion mode, which makes all molecules of one file to move apart and come together again.\n" +
            " 2. Animation mode, which makes the molecules rotating around the Y-axis as long as the user does not stop the animation.\n" +
            "\n" +
            "Apart from the molecule view, which was described above, there are two other tabs: One showing the content of the file, which was parsed to show the molecules and another one showing a summary of the file using charts.\n\n"+
            "This program is part of the Advanced Java for Bioinformatics course at University Tübingen in Germany and was created by Debora Jutz";
    private static final String[] atoms = new String[]{"All", "Carbon", "Hydrogen", "Nitrogen", "Oxygen", "Phosphor", "Sulfur", "Other"};
    private static final String[] coloring = new String[]{"Atoms", "Structure Types", "Monomers", "Nucleotides", "Amino Acids", "Sheets", "Helix", "Molecules", "Rainbow"};
    private static final String[] drawingStyle = new String[]{"Balls and Sticks", "Ribbon"};
    private static final Color[] rainbowColors = new Color[]{Color.PURPLE, Color.BLUE, Color.GREEN, Color.YELLOW, Color.ORANGE, Color.RED};

    public static String getAbout() {
        return about;
    }

    public static String[] getAtoms() {
        return atoms;
    }

    public static String[] getColoring() {
        return coloring;
    }

    public static String[] getDrawingStyle() {
        return drawingStyle;
    }

    public static Color[] getRainbowColors() {
        return rainbowColors;
    }
}
