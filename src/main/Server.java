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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.transport.connect.Connection;
import org.simpleframework.transport.connect.SocketConnection;
import org.simpleframework.util.thread.Scheduler;
import utils.time;

/**
 * Don't worry. Keep things simple, make things work. Worry tomorrow
 * about the problems you don't have today.
 * 
 * @author Nuno Brito, 1st of July 2014 in Darmstadt, Germany
 */
public class Server implements Container {

    Scheduler queue;
    Connection connection;
    public String webOutput = "";
    
       
    /**
     * Places the server into operation
     * @param args The arguments needed for running the server. We expect these
     * arguments to be similar as: "server 9999 repositories", where the first
     * parameter is to mention we want a server, the second parameter is the
     * port number to use and the third one explains which action to distribute.
     * Maybe in the future we don't need arguments at all.
     */
    void start(String[] args) {
        // first step: settings
        doSettings();
        // second step: instantiate server
        startServer(args[1]);
    }

    /**
     * Start our own settings for this server
     */
    private void doSettings(){
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
            System.err.println("Failed to open port for server: " + ex.getMessage());
      }
   }

    @Override
    public void handle(Request rqst, Response rspns) {
        Task task = new Task(rqst, rspns);
        queue.execute(task);
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
                processRequest("", response);
            }
            
            // give some feedback that a request occurred
            System.out.println(time.getDateTimeISO() + " Request for: " + rawText);
            
            // request for feeding with some new repositories to crawl
            if(rawText.equals("/get/repositories")){
                final String result = getUserToProcess();
                processRequest(result, response);
            }
        }
        
        /**
         * When a new request comes from the network, this is the method that
         * will conclude the reply. 
         * @param answer    The text given back to the requester
         * @param response  The object where the web request is held
         */
        void processRequest(final String answer, Response response){
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
        
 }
