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

import main.core;
import structure.User;

/**
 *
 * @author Nuno Brito, 3rd of July 2014 in Darmstadt, Germany
 */
public class status {

    /**
     * Delivers a text based report about our knowledge base statistics such as
     * the number of registered users, number of indexed repositories and other
     * metrics as deemed relevant.
     * @return An HTML portion of text ready to be displayed to the end-user
     */
    public static String get() {
        // get the counter values
        int countRep = utils.text.countLines(core.fileRepositories);
        int countUsers = utils.text.countLines(core.fileUsers);
        
        
        
        // list the users in queue (if any)
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
                    + "<br><br>\n"
                    + "Users in queue:<br>\n"
                    + usersInQueue;
        }
        
        
        // prepare the resulting message 
        String result =  ""
                + "<html>"
                + "<head></head>"
                + "<body>"

                + "Number of repositories: " + countRep
                + "<br>\n"
                + "Number of users: " + countUsers
                + usersInQueue
                
                + "</body>"
                + "</html>";
        
        // all done
        return result;
    }

    
}
