/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-06-30T14:04:22Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: core.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Where we keep global core of the program.</text> 
 */

package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 *
 * @author Nuno Brito, 30th of June 2014 in Darsmtadt, Germany
 */
public class core {
    static String username = "";
    static String password = "";
    static Preferences prefs;
    final static String filenameUsers = "users.txt";
    final static String filenameRepositories = "repositories.txt";
    
    // parameters used on the networked mode
    static public final String 
            webGetUser = "/request/user",
            webSubmitRepository = "/submit/repository";
    
    // check if our file already exists
    final static File fileUsers = new File(core.filenameUsers);
    final static File fileRepositories = new File(core.filenameRepositories);

    // our default server/client when needed
    final static Server server = new Server();
    final static Client client = new Client();
    
    // the class to handle repositories
    final static Repositories rep = new Repositories();
    
    
     /**
     * Returns the last line from a given text file
     * @param file  A file on disk 
     * @return The last line if available or an empty string if nothing
     * was found
     */
    static String getLastLine(File file){
        String result = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
            String line = "";
            while (line != null) {
                line = reader.readLine();
                if(line != null){
                    result = line;
                }
                // we don't care about the content, just move to the last line
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
        }
        // all done    
        return result;
    }
    
}
