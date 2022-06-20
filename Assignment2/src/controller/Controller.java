package controller;

import model.FourMer;
import model.Model;
import view.KmerView;
import model.InvalidKmerException;

import java.util.Formatter;
import java.util.Locale;
import java.util.TreeMap;

/**
 * The Controller class is one part to realize the Model-View-Controller principle.
 * The controller accepts input to be transformed in valid commands for @see Model.Model or @see View.KmerView
 * and therefore is an interface between @see Model.Model and @see View.KmerView.
 */
public class Controller {

    private Model model;
    private KmerView view;

    public Controller(){
        this.setModel(new Model(this));
        this.setView(new KmerView(this));
    }

    /**
     * Requests @see Model to get all valid 4mers from a DNA sequence and
     * produces a String to be displayed by @see KmerView according to the selection status of the checkBoxexs.
     * @param dnaSequence - the DNA sequence, from which the kmers are calculated.
     * @param isCodeVisible - true if the according Checkbox was selected and the hash code of 4mers shall be displayed.
     * @param isKmerVisible - true if the according Checkbox was selected and the 4mers shall be displayed.
     * @param isCountVisible - true if the according Checkbox was selected and the count of 4mers shall be displayed.
     * @return - the result of the Analysis as a String. The String contains code, kmer and count according to the given parameters.
     */
    public String getAnalysisResult(String dnaSequence, boolean isCodeVisible, boolean isKmerVisible, boolean isCountVisible){
        Formatter formatter = new Formatter();
        try{
            if(isCountVisible){
                // if count shall be displayed, the occurrences of the 4mers need to be calculated;
                // depending on the other parameters, the formatter produces different output.
                TreeMap<FourMer, Integer> result = model.countOccurrenceOfFourMers(model.calculate4Mers(dnaSequence));

                // show all
                if(isCodeVisible && isKmerVisible){
                    for (FourMer fourMer : result.keySet()) {
                        formatter = formatter.format(Locale.GERMAN, "%1$3s   %2$4s   %3$2s %n", fourMer.getHash(), fourMer.getKmer(), result.get(fourMer));
                    }
                }
                // show count and code, but not kmer
                else if (isCodeVisible){
                    for (FourMer fourMer : result.keySet()){
                        formatter = formatter.format(Locale.GERMAN, "%1$3s   %2$2s %n", fourMer.getHash(), result.get(fourMer));
                    }
                }
                // show count and kmer, but not code
                else if(isKmerVisible){
                    for (FourMer fourMer : result.keySet()){
                        formatter = formatter.format(Locale.GERMAN, "%1$3s   %2$2s %n", fourMer.getKmer(), result.get(fourMer));
                    }
                }
                else{
                    // show only count
                    for (FourMer fourMer : result.keySet()){
                        formatter = formatter.format(Locale.GERMAN, "%1$3s %n", result.get(fourMer));
                    }
                }
            }
            else{
                // if count is not requested to be displayed, only 4mers and their hash codes are calculated;
                FourMer[] result = model.calculate4Mers(dnaSequence);
                // show Code and kmer, but not count
                if(isCodeVisible && isKmerVisible){
                    for (int i = 0; i < result.length; i++){
                        FourMer fourMer = result[i];
                        formatter = formatter.format(Locale.GERMAN, "%1$3s   %2$4s %n", fourMer.getHash(), fourMer.getKmer());
                    }
                }
                // show code, but neither kmer nor count
                else if (isCodeVisible){
                    for (int i = 0; i < result.length; i++){
                        FourMer fourMer = result[i];
                        formatter = formatter.format(Locale.GERMAN, "%1$3s %n", fourMer.getHash());
                    }
                }
                // show kmer, but neither code nor count
                else if(isKmerVisible){
                    for (int i = 0; i < result.length; i++){
                        FourMer fourMer = result[i];
                        formatter = formatter.format(Locale.GERMAN, "%1$3s %n", fourMer.getKmer());
                    }
                }
                // if none of the boolean values is true, the empty string will be returned because nothing is requested to be displayed.
            }
            return formatter.toString();
        }
        catch (InvalidKmerException e){
            return e.getMessage();
        }
    }

    //Setter - are not supposed to be changed from outside, therefore private
    /**
     * Set the Model of this class to model.
     * @param model - Model to be set for this class. Shall not be null.
     */
    private void setModel(Model model){
         this.model = model;
    }

    /**
     * Set the View of this class to view.
     * @param view - View to be set for this class; shall not be null.
     */
    private void setView(KmerView view){
        this.view = view;
    }

    // Getter
    public KmerView getView(){
        return this.view;
    }
    public Model getModel(){
        return this.model;
    }

}
