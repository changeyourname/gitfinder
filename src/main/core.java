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

import distributed.Client;
import distributed.Server;
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
    public static String 
            username = "",
            password = "";
    
    // initialize the preferences object
    public static Preferences prefs = Preferences.userNodeForPackage(start.class);
    
    // parameters used on the networked mode
    static public final String 
            webGetUser = "/request/user", // gets a new user to process
            webSubmitRepository = "/repository/submit", // gives information about a given repository
            webFinishRepository = "/repository/finish", // says that no further changes will happen, close it up
            webStatus = "/status",
            webGetScript = "/script/client";
            
    // declare the files that we use on this software
    final static public String 
            filenameUsers = "users.txt",
            filenameRepositories = "repositories.txt";

    final static public File 
            fileUsers = new File(core.filenameUsers),
            fileRepositories = new File(core.filenameRepositories);

    // our default server/client when needed
    final static public Server server = new Server();
    final static public Client client = new Client();
    
    // the class to handle repositories
    final static public Repositories rep = new Repositories();
    
     
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
