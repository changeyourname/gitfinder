/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-07-03T08:31:56Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: status.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Provides a status report about the distributed software</text> 
 */

package distributed;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.core;
import structure.User;

/**
 *
 * @author Nuno Brito, 3rd of July 2014 in Darmstadt, Germany
 */
public class status {

    private static int 
        countRep = 0,
        countUsers = 0;
    
    /**
     * Delivers a text based report about our knowledge base statistics such as
     * the number of registered users, number of indexed repositories and other
     * metrics as deemed relevant.
     * @return An HTML portion of text ready to be displayed to the end-user
     */
    public static String get() {
        // get the counter values
        countRep = utils.text.countLines(core.fileRepositories);
        countUsers = utils.text.countLines(core.fileUsers);
        
        // get the percentage of user repositories that were already indexed
        String repositoriesIndexed = getIndexedRepositories();
        
        // list the users in queue (if any)
        String usersInQueue = getUsersInQueue();
        
        // prepare the resulting message 
        String result =  ""
                + "<html>"
                + "<head></head>"
                + "<body>"

                + "Number of users: " + countUsers
                + "<br>\n"
                + "Number of repositories: " + countRep
                + "<br>\n"
                + repositoriesIndexed
                
                + usersInQueue
                
                //+ getLanguageStats()
                
                + "</body>"
                + "</html>";
        
        // all done
        return result;
    }

    
    /**
     * Get the users that are currently in the queue, waiting to be processed
     */
    private static String getUsersInQueue(){
        String usersInQueue = "";
        for(User user : core.server.userWaitingList){
            // create the user list
            final String thisUser = user.getIdUser()
                    + " since "
                    + utils.time.getTimeFromLong(user.getTimeStamp())
                    + "<br>\n";
            // add the user to our list
            usersInQueue = usersInQueue.concat(thisUser);
        }
        // do we need to add a title
        if(usersInQueue.isEmpty() == false){
            usersInQueue = ""
                    + "<br>\n"
                    + "Users in queue:<br>\n"
                    + usersInQueue;
        }
        // all done
        return usersInQueue;
    }

    /**
     * Reports back the number of repositories that were already indexed.
     * @return An HTML result ready for printing.
     */
    private static String getIndexedRepositories() {
        // start by getting the last indexed id
        final String lastId = core.rep.getLastIndexedId();
        // now find the position of this id within the users.txt file
        int idPosition = main.users.getUserLine(lastId);
        // calculate the estimate percentage of users missing to be processed
        String estimation = utils.misc.getPercentage(idPosition, countUsers);
        // prepare the output of the result
        String result = "";
        // now add the requested information
        if(estimation.isEmpty() == false){
            result = "Percentage processed: " + estimation
                    + "%"
                    + "<br>\n";
        }
        
//        System.err.println("Last ID: " + lastId);
//        System.err.println("ID line: " + idPosition);
//        System.err.println("Users: " + countUsers);
        
        // all done
        return result;
    }
    
    
    /**
     * Computes the statistics about languages currently indexed in
     * the repository file
     * @return An HTML snippet ready to be displayed on a browser
     */
    static private String getLanguageStats(){
        // where we add the counter for different languages
        HashMap<String, Integer> counter = new HashMap();
        String result = "";
        BufferedReader reader;
        String line = "";
            try {
            reader = new BufferedReader(new FileReader(core.fileRepositories));
            while (line != null) {
                // get the language type
                int i1 = line.indexOf(" ");
                // do we have a possible value to process?
                if(i1 > -1){
                    final String temp = line.substring(i1 +1);
                    int i2 = temp.indexOf(" ");
                    final String language = temp.substring(0, i2);
                    // have we already indexed this key before?
                    if(counter.containsKey(language)){
                        // get the value, increment by one
                        int value = counter.get(language);
                        value++;
                        counter.put(language, value);
                    }else{
                        // just add a new one
                        counter.put(language, 1);
                    }
                }
                // read the nex line
                line = reader.readLine();
            }
            reader.close();
        } catch (IOException ex) {
            Logger.getLogger(status.class.getName()).log(Level.SEVERE, null, ex);
        }
           
        // sort the languages according to popularity    
        Map<String, Integer> sortedCounter = utils.thirdParty.MiscMethods.sortByComparator(counter);
            
        // now worry about creating an output with the collected results    
        for(String language : sortedCounter.keySet()){
            int count = counter.get(language);
            final String thisLine = language + ": " + count;
            result += thisLine + "<br>\n";
        }    
        
        if(counter.size()> 0){
            result = "<br>\n"
                    + "Language popularity:"
                    + "<br>\n" 
                    + result;
        }
        
        // all done    
        return result;
        }
    
}
