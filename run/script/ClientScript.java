/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-07-01T18:15:01Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: ClientScript.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Interact with the server to receive orders and deliver
the results.</text> 
 */

import distributed.Client;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import main.core;
import main.start;
import structure.Rep;

/**
 *
 * @author Nuno Brito, 1st of July 2014 in Darmstadt, Germany
 */
public class ClientScript implements Client{
    
    // the location and port where the server is located
    private String address = "";
    private final String errorConnectionRefused = "java.net.ConnectException: Connection refused";
    // how long should we wait before restarting the processing?
    final long maxWait = 1000 * 60 * 10;
    
    private final boolean canContinue = true;
    private Thread thread;
    /**
     * launch the client mode. Basically, we first try to find the server
     * that is mentioned as master and ask what we are supposed to do. If we
     * are unable to talk with the server, we fail as clients and let the user
     * know about this.
     * 
     * If the server is found, we receive instructions about what action should
     * be done and then proceed to execute. Most likely we will be requesting
     * small pieces of informprivate long watchDog = System.currentTimeMillis();
    ation, processing our share and submitting this
     * information uphill for storing on the server-side.
     * @param givenAddress URL and port number of where the server is located
     */
    @Override
    public void start(final String givenAddress){
        getLogin();
        System.out.println("Starting scripted client, attached to " + givenAddress);
        System.out.println("Version 0.2");
        address = givenAddress;
        final ClientScript thisClient = this;
            
        thread = new Thread(){
            
            @Override
            public void run(){
                while(true){
                    // get the next user that we want to process
                    final String nextUser = utils.internet.webget("http://" + address + core.webGetUser);
                    // check if had a successful connection or not
                    if(nextUser.equals(errorConnectionRefused)){
                       System.err.println("CL70 Error: Connection refused on " + address);
                       System.err.println("Retrying connection in 60 seconds..");
                        utils.time.wait(60);
                        continue;
                    }
                    // show a message to the end-user
                    System.out.println("\nProcessing " + nextUser);
                    // now process the assigned user
                    ArrayList<Rep> result = core.rep.getRepositories(nextUser);
                    
                    // a null reply means than error occurred
                    if(result == null){
                        doWait(10, "CL82 Error");
                        thisClient.start(givenAddress);
                        return;
                    }
                    
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

            /**
             * An error happened. This method will show a message and decreasing
             * timer until the requested waiting time has expired. This is mostly
             * used with network connections, which require trying several times.
             * @param i         Number of seconds to wait
             * @param message   Error code to show the end-user
             */
            private void doWait(final int i, final String message) {
                int count = i;
                        System.out.println(message + ", retrying again in "
                                + utils.text.pluralize(count, "second")
                        );
                        count--;
                        while(count>0){
                            utils.time.wait(1);
                            System.out.println(count + "..");
                            count--;
                        }
            }
        };
        // kickoff the thread
        thread.start();
    }
    
        
    /**
     * Sets the login details that will be used with the github API
     * @param args 
     */
    private static void setLoginDetails() {
        // get the values from save preferences if any
        core.username = core.prefs.get("username", "");
        core.password = core.prefs.get("password", "");

        // no need to continue if they were already set
        if(core.username.isEmpty() == false && core.password.isEmpty() == false){
            System.out.println("Using the login details of " + core.username);
            return;
        }


        System.out.println("Attention: No username nor password provided");
        System.out.println("In order to use the Github API in full speed, you need to provide a github login/password\n");


        try {
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            System.out.println("Your github login name: ");
            core.username = in.readLine();

            System.out.println("Your github password: ");
            core.password = in.readLine();

            // now save these into our preferences object
            core.prefs.put("username", core.username);
            core.prefs.put("password", core.password);
        } catch (IOException ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Takes care of the user credentials that will be used with GitHub.
     */
    private void getLogin() {
        // do we have some kind of text file with the user details?
        File file = new File("password.txt");
        // does the password file exists?
        if(file.exists()==false){
            // just use the normal user/password procedure
            setLoginDetails();
            return;
        }
        // otherwise read the text file and use these details instead
        final String lines = utils.files.readAsString(file);
        // now split each line into a field
        final String[] line = lines.split("\n");
        // we expect the first line to have the username
        core.username = line[0];
        // and the second line to have the password
        core.password = line[1];
        // give some output
        System.out.println("Using credentials of " + core.username);
        // all done
    }
    
      
}
