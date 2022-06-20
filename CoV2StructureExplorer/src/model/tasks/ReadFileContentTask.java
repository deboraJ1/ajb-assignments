package model.tasks;

import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import model.pdbaccess.FileParser;
import model.pdbaccess.PDBWebClient;
import org.apache.commons.lang.NullArgumentException;

import java.io.IOException;

public class ReadFileContentTask extends Task<String> {

    StringProperty filename;
    PDBWebClient webClient;

    public ReadFileContentTask(StringProperty filename, PDBWebClient webClient){
        if(filename != null ){
            this.filename = filename;
        }
        else {
            //cancel task if no name is specified.
            setException(new NullArgumentException("File with filename null can not be read. Please select another file from the list."));
            cancel(true);
        }
        if(webClient != null){
            this.webClient = webClient;
        }
        else{
            setException(new NullArgumentException("Web Client was not found, therefore file with given filename can not be read. \n Sorry. Please try again later."));
            cancel(true);
        }
        running();
    }


    @Override
    protected String call() throws Exception {
        // filename can not be null or blank
        if(!FileParser.isFileCreated(this.filename.getValue())){
            updateProgress(10, 100);
            //only create file if it is not present in the default directory.
            try{
                FileParser.createFileFromStream(webClient.getStreamOfID(filename.getValue()), filename.getValue());
            }
            catch (IOException e){
                this.setException(e);
                this.cancel(true);
            }
        }
        updateProgress(30, 100);
        String output = FileParser.getContentOfFile(filename.getValue());
        updateProgress(90, 100);
        if(output == null){
            //shall not cause an error but just be displayed in output area instead.
            output = "Content of file could not be read.";
        }
        else if(output.isBlank()){
            output = "Nothing to show because the selected file was empty.";
        }
        updateProgress(99, 100);
        return output;
    }
}
