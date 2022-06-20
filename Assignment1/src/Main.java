public class Main {


    public static void main(String[] args) {
        FastAFileParser reader;

        //parse command line:
        if (args != null) {
            if (args.length == 1) {
                reader = new FastAFileParser(args[0]);
            }
            else {
                //there are too few or too many arguments
                System.out.println("Got " + args.length + " arguments");
                System.out.println("Code4Mers expects exactly ONE input argument: the path to the fastA input file. PLEASE SPECIFY THE INPUT FILE PATH.");
            }
        }
        else {
            // no argument was specified.
            System.out.println("You need to specify an input argument: the path to the fastA input file.");
        }

    }
}

