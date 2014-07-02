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

import java.io.File;
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
import structure.User;

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
    private final ArrayList<Rep> repWaitingList = new ArrayList();
    public final ArrayList<User> userWaitingList = new ArrayList();
    
    // where we save the state of users being processed (fail safe procedure)
    private File fileQueueUsers = new File("queueUsers.txt");
    
       
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
            
            // load the previous users in our queue list (if any)
            userLoadState();
            
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
        // start with deleting this user from our user waiting list
        userWaitingListDelete(userId);

        // create a temporary holder to check the repository waiting list
        ArrayList<Rep> newList = new ArrayList();
        // iterate all the repositories in our waiting list
        for(Rep rep : repWaitingList){
            // do we have a match with the deleted id?
            if(utils.text.equals(rep.getIdUser(), userId)){
                // now add this item on our list
                newList.add(rep);
            }
        }
        
        // no need to proceed if nothing was found
        if(newList.isEmpty()==false){
            // we now proceed to write up the items onto the repositories.txt file
            for(Rep rep : newList){
                final String line = rep.getOneline();
                // write up the new line in our repository text file
                core.rep.addNewRepositories(line + "\n");
                // give some feedback to the end user
                System.out.println("web: " + line);
                // remove from our waiting list
                repWaitingList.remove(rep);
            }
        }

        // place a visual divisory to ease human-identification
        System.out.println("====> " + userId);
        // all done
    }

    /**
     * Remove a given user id from our waiting list
     * @param userId    The user id to remove
     */
    private void userWaitingListDelete(final String userId){
        int result = -1;
        // go through all users registered
        for(User user : userWaitingList){
            // do we have a match?
            if(utils.text.equals(userId, user.getIdUser())){
                // it matches, get the index number as reference
                result = userWaitingList.indexOf(user);
                // no need to continue
                break;
            }
        }
        // did we found something?
        if(result == -1){
            // seems not, then no action will take place
            return;
        }
        // we have a valid result, delete the mentioned user
        userWaitingList.remove(result);
        // save the current state
        userSaveState();
    }
    
    /**
     * Delivers a text based report about our knowledge base statistics such as
     * the number of registered users, number of indexed repositories and other
     * metrics as deemed relevant.
     * @return An HTML portion of text ready to be displayed to the end-user
     */
    String getStatus() {
        // get the counter values
        int countRep = utils.text.countLines(core.fileRepositories);
        int countUsers = utils.text.countLines(core.fileUsers);
        
        // list the users in queue (if any)
        String usersInQueue = "";
        for(User user : userWaitingList){
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

    /**
     * Adds a new user to our queue for processing
     * @param user The user to be added
     */
    void addUserToQueue(User user) {
        for(User thisUser : userWaitingList){
            // was this user already added on our queue?
            if(utils.text.equals(thisUser.getIdUser(), user.getIdUser())){
                // Seems the case, no need to proceed
                return;
            }
        }
        // no duplicates, we can add the user
        userWaitingList.add(user);
        // save the current state
        userSaveState();
    }
    
    /**
     * Saves the current list of users waiting to be processed on disk
     */
    void userSaveState(){
        String result = "";
        
        // if we have no queue list, just delete the file
        if(userWaitingList.isEmpty()){
            // delete the file (doesn't check if it is open or not)
            fileQueueUsers.delete();
            // nothing else to do here
            return;
        }
        
        // iterate all users in our waiting list
        for(User user : userWaitingList){
            result = result.concat(user.getIdUser() + "\n");
        }
        // write them up to a file on disk
        utils.files.SaveStringToFile(fileQueueUsers, result);
    }
    
    /**
     * Recovers the list of waiting users when the server execution terminated
     * abruptly.
     */
    void userLoadState(){
        // preflight check
        if(fileQueueUsers.exists() == false){
            // no need to continue if exists no backup file
            return;
        }
        // now read the contents of this file to a string
        final String users = utils.files.readAsString(fileQueueUsers);
        // and finish by placing them on our queue
        for(final String user : users.split("\n")){
            // create the user object and ensure it gets processed with priority
            User thisUser = new User(user, true);
            // place this on our queue
            addUserToQueue(thisUser);
            System.out.println("Added to waiting list: " + user);
        }        
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
            
            // request for feeding with some new repositories to crawl
            if(rawText.equals(core.webGetUser)){
                final User result = getUserToProcess();
                if(result == null){
                    answerRequest("", response);
                }else{
                    answerRequest(result.getIdUser(), response);
                }
                // all done
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
            
            // did we received data from a client?
            if(rawText.startsWith(core.webStatus)){
                final String result = core.server.getStatus();
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
        private User getUserToProcess(){
            // do we have any old user that wasn't processed yet?
            for(User user : core.server.userWaitingList){
                if(user.isOld()){
                    // yep, we have one. Give it back for processing
                    return user;
                }
            }
            
            // no old users to process, just get the next user in line
            final String answer = core.rep.getNextUser();
            // if null, just provide an empty reply
            if(answer == null){
                return null;
            }
            
            // create a new user
            User user = new User(answer);
            // place this user in our waiting queue
            core.server.addUserToQueue(user);
            // all done
            return user;
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
        final String userId = items[0];
        final String language = utils.text.htmlDecode(items[2]);
        final String description = utils.text.htmlDecode(items[3]);
        
        // are we permitted to add this information on the repository?
        boolean notAllowed = true;
        // iterate through all the users currently being processed
        for(User user : core.server.userWaitingList){
            if(utils.text.equals(userId, user.getIdUser())){
                // we found a match, we're good to proceed
                notAllowed = false;
                break;
            }
        }
        // are allowed or not to add this information?
        if(notAllowed){
            System.out.println("SR411 - Not allowed to write rep for " + userId);
            return "504";
        }
        
        // create the new repository to be placed on our waiting list
        Rep rep = new Rep();
        rep.setIdUser(userId);
        rep.setIdRepository(items[1]);
        rep.setLanguage(language);
        rep.setDescription(description);
        // now add this repository to our queue list
        core.server.addNewWaitingItem(rep);
        // all ok, let's conclude
        return "200";
    }

    /**
     * Process the special web request that informs the server that no further
     * repositories from a given user need to be processed.
     * @param rawText   The web request containing the user identification
     * @return  The result message, 200 when OK or 50x when something went wrong
     */
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
        
        // all done
        return "200";
    }

 }
