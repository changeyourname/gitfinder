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

package main;

import java.util.ArrayList;
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
                System.out.println("Client is running");
                
                while(true){
                    // get the next user that we want to process
                    final String nextUser = utils.internet.webget("http://" + address + core.webGetUser);
                    // check if had a successful connection or not
                    if(nextUser.equals(errorConnectionRefused)){
                        System.err.println("Error: Connection refused on " + address);
                        break;
                    }
                    
                    System.out.println("Processing: " + nextUser);
                    
                    // now process the assigned user
                    ArrayList<Rep> result = core.rep.getRepositories(nextUser);
                    
                    // we got an empty result, move to the next processing then
                    if(result.isEmpty()){
                        System.out.println("No repositories for " + nextUser + "\n");
                        continue;
                    }
                    // kevwil/c-ration Visual Studio Configuration server based on @typesafehub/config and @playframework/playframework which serves up static and dynamic config based on a file hierarchy.
                    /// iterate each repository item
                    for(final Rep rep : result){
                        // from where we will submit the item to the server
                        final String answer = utils.internet.webget("http://" 
                            + address + core.webSubmitRepository
                        + rep.getWebSubmit());
                        System.out.println("---->" + rep.getWebSubmit());
                    }
                    
                    
                    // if we have something to share, send it upstream
//                    final String answer = utils.internet.webget("http://" 
//                            + address + core.webSubmitRepository);
                    //utils.time.wait(5);
                    //System.exit(111);
                }
            }
        };
        // kickoff the thread
        thread.start();
    }
    
    
    
    
    
    
    
    
}