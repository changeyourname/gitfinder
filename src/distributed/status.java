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
        
        System.err.println("Last ID: " + lastId);
        System.err.println("ID line: " + idPosition);
        System.err.println("Users: " + countUsers);
        
        // all done
        return result;
    }
    
    
}
