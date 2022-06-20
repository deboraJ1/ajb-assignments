package model;

import presenter.WindowPresenter;

import java.io.*;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.List;

public class Sequences extends ArrayList<Sequences.HeaderSequence> {

    private BufferedReader reader;

    /**
     * Record HeaderSequence: contains the parameters header and sequence, both as String.
     * Neither header nor sequence are supposed to be null. If so, a NullPointerException will be thrown (default for all records).
     */
    public static record HeaderSequence(String header, String sequence){

    }

    /**
     * Get the text to be displayed according to the headers and sequences which were read from a fastA file.
     * @param showHeaders - a boolean describing if headers shall be shown in the output text
     * @param showSequences - a boolean describing if sequences shall be shown in the output text
     * @return the text which shall be displayed according to the given parameters and the read headers and sequences.
     */
    public String getText (boolean showHeaders, boolean showSequences){
        String text = "";
        for(int i = 0; i < this.size(); i++){
            if(showHeaders && showSequences){
                text += this.get(i).header + "\n" + this.get(i).sequence + "\n \n";
            }
            //not both are true but maybe only one:
            else if(showHeaders){
                text += this.get(i).header + "\n \n";
            }
            else if(showSequences){
                text += this.get(i).sequence + "\n \n";
            }
            //else: none was true
        }
        return text;
    }

    /**
     * Get a List containing all headers of the sequences read from last fastA file.
     * @return all headers which were read from last file in a List.
     */
    public List<String> getHeader (){
        List<String> headers = new ArrayList<>();
        for(int i = 0; i < this.size(); i++){
            headers.add(this.get(i).header);
        }
        return headers;
    }

    /**
     * Read the file specified with fileName
     * @param fileName - name of file to be read
     * @throws IOException - Whenever a problem occurs on creating the reader or when fileName is null or blank
     */
    public void read (String fileName) throws IOException{
        if(fileName != null && fileName.isBlank()){
            this.reader = new BufferedReader(new FileReader(fileName));
        }
        else{
            throw new FileNotFoundException("Please specify a valid path to the fastA file you want to be processed. The file path is not supposed to be empty or null.");
        }
    }

    /**
     * Read the file specified as parameter.
     * @param file - file to be read
     * @throws IOException - Whenever a problem occurs on creating the reader or when file is null
     */
    public void read (File file) throws IOException{
        if(file != null){
            this.reader = new BufferedReader(new FileReader(file));
        }
        else{
            throw new FileNotFoundException("Please specify a valid fastA file you want to be processed. The file is not supposed to be null.");
        }
    }

    /**
     * reads a fastA file and extracts all DNA sequence as header-sequence pair, which are given in the fastA file.
     * For each header-sequence pair a HeaderSequence instance is created where the sequence is stored in uppercase.
     */
    public void extractDNAs() {
        boolean dnaSeqReading = false;
        try{
            //extract dna sequence: name of sequence starts with >, dna does not.
            String nextLine = reader.readLine();
            String header = null;
            String sequence = null;

            while (nextLine != null){
                if(nextLine.startsWith(">")){

                    //one header sequence pair is completely read --> save as record
                    if(header != null && sequence != null){ //if only header were != null, there is an header without sequence and shall not be stored.
                        sequence.toUpperCase();
                        //store header-sequence pair in List
                        this.add(new HeaderSequence(header, sequence));
                        //reset
                        header = null;
                        sequence = null;
                    }

                    header = nextLine;
                    nextLine = reader.readLine();
                    dnaSeqReading = false;
                }
                else{
                    if(dnaSeqReading){
                        sequence +=nextLine;
                    }
                    else{
                        sequence = nextLine;
                        dnaSeqReading = true;
                    }
                    nextLine = reader.readLine();
                }
            }
        }
        catch (IOException e){
            System.out.println("Sorry a problem occurred while reading the file.");
        }
    }
}
