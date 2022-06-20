package model.pdbaccess;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonString;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PDBWebClient is a class to provide connection to a REST service.
 * Connections are created to two URLs: one to get all entry IDs and the second to request a file of a specific PDB ID, which needs to be within the result of all returned entry IDs.
 */
public class PDBWebClient {

    private static final String URL_TO_ENTRY_IDS = "https://data.rcsb.org/rest/v1/holdings/current/entry_ids";
    private static final String URL_TO_CIF_FILE = "https://files.rcsb.org/download/%s" + FileParser.FILE_EXTENSION;

    private ListProperty<String> entryIDs;
    private final ListProperty<String> sarsCovEntryIDs;

    /**
     * Constructor of the class PDBWebClient. Initializes the list of entry IDs from the REST interface of the rcsb server.
     * @throws IOException - if reading from the URL fails
     */
    public PDBWebClient() throws IOException {
        requestAllPDBIDs();
        //even though its hard coded and wont, ListProperty is chosen instead of List because one could improve the program so that Sars cov 2 IDs are derived from REST server and then the List could change.
        this.sarsCovEntryIDs = new SimpleListProperty<String>(FXCollections.observableArrayList(Arrays.asList("6ZMO", "6ZOJ", "6ZPE", "6ZP5", "6ZP4", "6ZP7", "6ZOX", "6ZOW", "6ZOZ", "6ZOY", "6ZOK", "6ZON", "6ZP1", "6ZP0", "6ZP2",
                "5R84", "5R83", "5R7Y", "5R80", "5R82", "5R81", "5R8T", "5R7Z", "5REA", "5REC")));
    }

    /**
     * Request the REST interface to get all entry IDs, which are then stored in a list.
     * All entry Ids, which were in the list before are removed.
     * @throws IOException - if an error occurs when trying to open a connection to the default URL.
     */
    public void requestAllPDBIDs() throws IOException {
        List<String> simpleList = new ArrayList<>();

        // get entry IDs
        URL url = new URL(URL_TO_ENTRY_IDS);
        try (JsonReader reader = Json.createReader(new InputStreamReader(getFromURL(url)))){
            reader.readArray().stream().map(v -> ((JsonString)v).getString()).forEach(simpleList::add);
        }
        this.entryIDs = new SimpleListProperty<>(FXCollections.observableArrayList(simpleList));
    }

    /**
     * Create a new File containing all information which were retrieved from the REST interface
     * @param pdbID
     * @return the input stream which was read from the REST interface about the given pdbID
     * @throws IOException
     */
    public InputStream getStreamOfID(String pdbID) throws IOException{
        if(pdbID != null && !pdbID.isBlank()){
            pdbID = pdbID.toLowerCase();

            URL url = new URL(String.format(URL_TO_CIF_FILE, pdbID));
            return getFromURL(url);
        }
        else{
            throw new FileNotFoundException("Error requesting InputStream: PDB ID is not valid if it is null or blank");
        }
    }

    /**
     * Get the InputStream from URL provided.
     * @param url - the URL to which a connection shall be established and to get the InputStream from. Is not supposed to be null.
     * @return - the InputStream, null if url is null
     * @throws IOException - if an error occurs when trying to open a connection to URL url.
     */
    private InputStream getFromURL(URL url) throws IOException {
        if(url != null) {
            URLConnection connection = url.openConnection();
            connection.connect();
            return connection.getInputStream();
        }
        else{
            return null;
        }
    }


    /**
     * Get a ListProperty holding all entry IDs, which can be accessed at the REST interface of the base URL of this class.
     * @return - a ListProperty of strings, where each String is the ID of one entry.
     *          The list might be empty, e.g. if an error occurred when reading from the base URL
     */
    public ListProperty<String> allEntryIDsProperty() throws IOException {
        requestAllPDBIDs();
        return entryIDs;
    }

    public ListProperty<String> sarsCovEntryIDsProperty() {
        return sarsCovEntryIDs;
    }
}
