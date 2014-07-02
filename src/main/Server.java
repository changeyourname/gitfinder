/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-07-01T10:50:11Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: Server.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Provides a server for distributed processing of the 
repositories indexing.</text> 
 */
package main;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.simpleframework.util.thread.Scheduler;
import structure.Rep;

/**
 * Don't worry. Keep things simple, make things work. Worry tomorrow
 * about the problems you don't have today.
 * 
 * @author Nuno Brito, 1st of July 2014 in Darmstadt, Germany
 */
public class Server implements Container {

    Scheduler queue;
    Connection connection;
    
    // where we place the temporary repositories that will be written
    private ArrayList<Rep> repWaitingList = new ArrayList();
    
       
    /**
     * Places the server into operation
     * @param args The arguments needed for running the server. We expect these
     * arguments to be similar as: "server 9999 repositories", where the first
     * parameter is to mention we want a server, the second parameter is the
     * port number to use and the third one explains which action to distribute.
     * Maybe in the future we don't need arguments at all.
     */
    void start(String[] args) {
        // instantiate the server
        startServer(args[1]);
    }

  
     /** Start our web server instance
     * @param portNumber the port of communications (typically 80 for Internet)
     */     
   public void startServer(String portNumber){
           try {
            // get the port number
            int serverPort = Integer.parseInt(portNumber);
            
            // launch our server instance
            queue = new Scheduler(20);
            Container container = this; //new WebServer(queue);
            connection = new SocketConnection(container);
            final SocketAddress address = new InetSocketAddress(serverPort);

            // do the connection itself
            connection.connect(address);
            
            System.out.println("Server available on port " + portNumber);
            
         } catch (IOException ex) {
            System.err.println("Error: Failed to open port " + portNumber 
                    + ". Reason: " + ex.getMessage());
            System.exit(500);
      }
   }

    @Override
    public void handle(Request rqst, Response rspns) {
        Task task = new Task(rqst, rspns);
        queue.execute(task);
    }
    
    
        /**
     * This method receives the information from a given client and places it on
     * a queue. The queue holds all pending information that hasn't yet been written
     * to the repository list. This is intentional, the goal is to only write
     * information about repositories after a user has been fully analysed.
     * @param rep 
     */
    public void addNewWaitingItem(Rep rep) {
        // get the unique identifier for this repository
        final String id = rep.getUID();
        // we now iterate the current IDs to see if a duplicate exists
        for(final Rep thisRep : repWaitingList){
            // if we have a duplicate, no need to proceed
            if(utils.text.equals(thisRep.getUID(), id)){
                return;
            }
        }
        // no duplicates found, add up this rep
        core.server.repWaitingList.add(rep);
    }

    /**
     * The final step. In previous steps the client was providing information
     * about the repositories, now we will write this information on the text
     * file because we've received a signal that we can proceed
     * @param userId The user id that we will look for in our waiting list
     */
    void finishRepository(final String userId) {
        // create a temporary holder
        ArrayList<Rep> newList = new ArrayList();
        // iterate all the repositories in our waiting list
        for(Rep rep : repWaitingList){
            // do we have a match?
            if(utils.text.equals(rep.getIdUser(), userId)){
                // add this item on our list
                newList.add(rep);
            }
        }
        // no need to proceed if nothing was found
        if(newList.isEmpty()){
            return;
        }
        // now proceed to write up the items
        for(Rep rep : newList){
            final String line = rep.getOneline();
            // write up the new line in our repository text file
            core.rep.addNewRepositories(line + "\n");
            // give some feedback to the end user
            System.out.println("web: " + line);
            // remove from our waiting list
            repWaitingList.remove(rep);
        }
        
        System.out.println("====> " + userId);
        
        //        // at this point we don't filter much the result and assume everything is correct
//        final String line = items[0] + "/" + items[1] 
//                + " " + language + " " + description;
//        
//        // write up this new line
//        core.rep.addNewRepositories(line + "\n");
//        // do some output to let everyone know there is some action going            
//        System.out.println("web: " + line);
        
        
    }
    
}

 class Task implements Runnable {
  
      private Response response = null;
      private Request request = null;
    
      
      public Task(Request request, Response response) {
         this.response = response;
         this.request = request;
      }

      
        @Override
        public void run() {
            // get what we are trying to run
            String rawText = request.getTarget();
            
            // requests to ignore such as favicon (by browsers)
            if(rawText.equals("/favicon.ico")){
                answerRequest("", response);
                return;
            }
            
            // give some feedback that a request occurred
            //System.out.println(time.getDateTimeISO() + " " + rawText);
            
            // request for feeding with some new repositories to crawl
            if(rawText.equals(core.webGetUser)){
                final String result = getUserToProcess();
                answerRequest(result, response);
                return;
            }
            
            // did we received data from a client?
            if(rawText.startsWith(core.webSubmitRepository)){
                final String result = submitRepository(rawText);
                answerRequest(result, response);
                return;
            }
            
            // did we received data from a client?
            if(rawText.startsWith(core.webFinishRepository)){
                final String result = finishRepository(rawText);
                answerRequest(result, response);
                return;
            }
            
           
            // all done
            answerRequest("404", response);
        }
        
        /**
         * When a new request comes from the network, this is the method that
         * will conclude the reply. 
         * @param answer    The text given back to the requester
         * @param response  The object where the web request is held
         */
        void answerRequest(final String answer, Response response){
            try {
                  // the variable where we write the output for our requester
                  PrintStream body;
                  // get access to where we can write the answer
                  body = response.getPrintStream();
                  // write what we want to display the end-user
                  body.println(answer);
                  // all done, close our streams
                  body.close();
                  response.close();
                } catch (IOException ex) {
                  Logger.getLogger(Task.class.getName()).log(Level.SEVERE, null, ex);
                }
        }
        
        
        /**
         * Gets some users from our list to process
         */
        private String getUserToProcess(){
            // get the next user in line from our repository tracker
            final String answer = core.rep.getNextUser();
            // if null, just provide an empty reply
            if(answer == null){
                return "";
            }
            // otherwise, give the name
            return answer;
        }

        /**
         * Handles the case when a client submits the information about a
         * given repository back into our server. This method assumes that
         * information is given through the URL on the following format:
         * /repository/submit/userid/repositoryid/language/description
         * @param rawText
         * @return 
         */
    private String submitRepository(final String rawText) {
        // remove the initial keyword from our text
        String content = rawText.substring(core.webSubmitRepository.length());
        // handle a possible error when no data is provided
        if(content.isEmpty()){
            System.out.println("Error 501: " + content);
            return "501";
        }
        // remove initial slash if one is provided
        if(content.startsWith("/")){
            content = content.substring(1);
        }
        // test again if the result is empty or not
        if(content.isEmpty()){
            System.out.println("Error 502: " + content);
            return "502";
        }
        
        // now partition the URL into separate blocks
        String[] items = content.split("/");
        
        // we expect a specific number of parameters
        if(items.length != 4){
            System.out.println("Error 503: " + content);
            return "503";
        }
        
        // try to fix part of the conversion
        final String language = utils.text.htmlDecode(items[2]);
        final String description = utils.text.htmlDecode(items[3]);
        
        // create the new repository to be placed on our waiting list
        Rep rep = new Rep();
        rep.setIdUser(items[0]);
        rep.setIdRepository(items[1]);
        rep.setLanguage(language);
        rep.setDescription(description);
        // now add this repository to our queue list
        core.server.addNewWaitingItem(rep);
        
//        // at this point we don't filter much the result and assume everything is correct
//        final String line = items[0] + "/" + items[1] 
//                + " " + language + " " + description;
//        
//        // write up this new line
//        core.rep.addNewRepositories(line + "\n");
//        // do some output to let everyone know there is some action going            
//        System.out.println("web: " + line);

        // all ok, let's conclude
        return "200";
    }

    private String finishRepository(final String rawText) {
        // remove the initial keyword from our text
        String content = rawText.substring(core.webSubmitRepository.length());
        // handle a possible error when no data is provided
        if(content.isEmpty()){
            System.out.println("Error 501: " + content);
            return "501";
        }
        // remove initial slash if one is provided
        if(content.startsWith("/")){
            content = content.substring(1);
        }
        // test again if the result is empty or not
        if(content.isEmpty()){
            System.out.println("Error 502: " + content);
            return "502";
        }
        
        // now signal that we can write all the entries onto the text file
        core.server.finishRepository(content);
        
        //System.out.println("====>" + content);
        return "200";
    }

 }
