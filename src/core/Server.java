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
package core;

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

/**
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
        //System.out.println(args[1]);
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
  
      private final Response response;
      private final Request request;
 
      public Task(Request request, Response response) {
         this.response = response;
         this.request = request;
      }

      
        @Override
        public void run() {
            // get what we are trying to run
            String rawText = request.getTarget();
            
            // requests to ignore
            if(rawText.equals("/favicon.ico")){
                processRequest("", response);
            }
            
            // give some feedback that a request occurred
            System.out.println("Request for: " + rawText);
            
            // request for feeding with some new repositories to crawl
            if(rawText.equals("/get/repositories")){
                processRequest("Getting list of repositories", response);
            }
            
            
        }
        
        
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
        
        
 }
