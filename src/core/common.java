/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-06-30T14:04:22Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: common.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Where we keep global common of the program.</text> 
 */

package core;

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
public class common {
    static String username = "";
    static String password = "";
    static Preferences prefs;
    final static String filenameUsers = "users.txt";
    final static String filenameRepositories = "repositories.txt";
    
    // check if our file already exists
    final static File fileUsers = new File(common.filenameUsers);
    final static File fileRepositories = new File(common.filenameRepositories);

    
    
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
