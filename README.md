# ajb-assignments
@author Debora Jutz

Solved assignments from the course 'Advanced Java for Bioinformatics'

The final project to build a Corona Virus Structure Explorer is described more detailed in the following:

This program is only running when there is an internet connection. If there is no internet connection, this program will show an error message, informing the user that the PDB server can not be reached.
The Window will still appear, but the PDB entry IDs will not be loaded.
Otherwise the program supports loading PDB files from the server as well as storing them locally. The described models of a file can be read, parsed and shown.
Atoms are per default shown in their respective standard colors but the coloring can be changed to show different Structures, all helixes only, all sheets only, different molecules as well as in rainbow colors.
Also are molecules per default shown in atom and stick view.

Two kinds of animations are provided to help the user to get a better understanding of the program. These are:
 1. Explosion mode, which makes all molecules of one file to move apart and come together again.
 2. Animation mode, which makes the molecules rotating around the Y-axis as long as the user does not stop the animation.

Apart from the molecule view, which was described above, there are two other tabs: One showing the content of the file, which was parsed to show the molecules and another one showing a summary of the file using charts.
