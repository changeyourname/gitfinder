/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-07-01T18:15:01Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: Client.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Interact with the server to receive orders and deliver
the results.</text> 
 */

package distributed;

import java.util.ArrayList;
import main.core;
import structure.Rep;

/**
 *
 * @author Nuno Brito, 1st of July 2014 in Darmstadt, Germany
 */
public class Client {
    
    // the location and port where the server is located
    private String address = "";
    private final String errorConnectionRefused = "java.net.ConnectException: Connection refused";

    /**
     * launch the client mode. Basically, we first try to find the server
     * that is mentioned as master and ask what we are supposed to do. If we
     * are unable to talk with the server, we fail as clients and let the user
     * know about this.
     * 
     * If the server is found, we receive instructions about what action should
     * be done and then proceed to execute. Most likely we will be requesting
     * small pieces of information, processing our share and submitting this
     * information uphill for storing on the server-side.
     * @param givenAddress URL and port number of where the server is located
     */
    public void start(final String givenAddress){
        System.out.println("Starting the client mode, attached to " + givenAddress);
        address = givenAddress;
        
        Thread thread = new Thread(){
            @Override
            public void run(){
                while(true){
                    // get the next user that we want to process
                    final String nextUser = utils.internet.webget("http://" + address + core.webGetUser);
                    // check if had a successful connection or not
                    if(nextUser.equals(errorConnectionRefused)){
                        System.err.println("Error: Connection refused on " + address);
                        break;
                    }
                    
                    // introduce an artificial delay to helps analyse this index
                    //utils.time.wait(2);
                    
                    System.out.println("\nProcessing " + nextUser);
                    
                    // now process the assigned user
                    ArrayList<Rep> result = core.rep.getRepositories(nextUser);
                    
                    // we got an empty result, move to the next processing then
                    if(result.isEmpty()){
                        // inform the end-user about what we (didn't) found
                        System.out.println("No repositories for " + nextUser);
                        // send the finish message
                        final String answer = utils.internet.webget("http://" 
                            + address + core.webFinishRepository
                        + "/" + nextUser);
                        // jump to the next user
                        continue;
                    }

                    // iterate each repository item
                    for(final Rep rep : result){
                        // from where we will submit the item to the server
                        final String answer = utils.internet.webget("http://" 
                            + address + core.webSubmitRepository
                        + rep.getWebSubmit());
                        System.out.println(answer + " ---> " + rep.getWebSubmit());
                    }
                    // do the concluding command
                    if(result.size()>0){
                        final String answer = utils.internet.webget("http://" 
                            + address + core.webFinishRepository
                        + "/" + nextUser);
                    }
                    
                }
            }
        };
        // kickoff the thread
        thread.start();
    }
}
