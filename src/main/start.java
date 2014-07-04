/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-06-24T19:45:34Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: start.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> A project for handling the crawl of github.</text> 
 */

package main;

import distributed.Client;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nuno Brito, 28th of May 2014 in Paris, France.
 */
public class start {

    
    // the root folder where all code ends up stored
    final static File folderCode = new File("./code");
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        
        // are we looking to index the users?
        if(args[0].equalsIgnoreCase("users")){
            // ensure that we have the needed credentials for github
            setLoginDetails();
            System.out.println("Indexing the users");
            users.launchCrawling();
            return;
        }
        
        // or are we interested in indexing repositories?
        if(args[0].equalsIgnoreCase("repositories")){
            // ensure that we have the needed credentials for github
            setLoginDetails();
            System.out.println("Indexing repositories");
            core.rep.launchRepositoryIndexing();
            return;
        }
        
               
        // or are we interested in grabbing files from repositories?
        if(args[0].equalsIgnoreCase("grab")){
            // ensure that we have the needed credentials for github
            setLoginDetails();
            System.out.println("Grabbing files - NOT IMPLEMENTED");
            //launchGrabFiles(args[1]);
            return;
        }
               
        // or are we interested in grabbing files from repositories?
        if(args[0].equalsIgnoreCase("reset")){
            System.out.println("Resetting user details");
            core.prefs.remove("username");
            core.prefs.remove("password");
            System.out.println("Done! Your details were erased from this computer");
            return;
        }
        
        // or are we interested in grabbing files from repositories?
        if(args[0].equalsIgnoreCase("server")){
            System.out.println("Starting the server");
            core.server.start(args);
            return;
        }
  
        // or are we interested in grabbing files from repositories?
        if(args[0].equalsIgnoreCase("client")){
            launchClient(args);
            return;
        }
  
        
        // no arguments specified, show a simple syntax usage
        System.out.println("Syntax usage: java -jar gitfinder.java users|repositories username password"
                + "\n"
                + "Example:\n"
                + "java -jar gitfinder.jar users");
    }
    
    /**
     * Launches the client that will be running on this instance of the software.
     * To launch each client we will ask the server to provide us with the
     * source code that we want to run.
     */
    private static void launchClient(final String[] args){
        // let's get the source code that we want to 
        final String location = "https://raw.githubusercontent.com/triplecheck/gitfinder/master/run/script/ClientScript.java";
        final String sourceCode = utils.internet.getTextFile(location);
        // now create a new object with the client source code
        Client newClass = (Client) utils.bytecode.getObjectNoPackage(sourceCode, Client.class.getCanonicalName());
        // run it up
        System.out.println("Running the script client");
        newClass.start(args[1]);
    }
   

    /**
     * When given a users name, go after the repositories where the users
 is involved and grab a copy
     */
    private static void launchGrabFiles(final String usernameTarget) {
        if(usernameTarget == null){
            System.out.println("You need to specify a user name");
            return;
        }
        // we have a users name, let's start
        System.out.println("Processing " + usernameTarget);
        
        // what is our folder?
        File thisFolder = new File(folderCode, usernameTarget);
       
    }

    /**
     * When given a repository name, this method will download the files
     * onto the respective folder on disk
     * @param usernameTarget    Owner of the repository
     * @param repositoryName    The name of the repository
     */
    private static void processRepository(final String usernameTarget, 
            final String repositoryName) {
      
        // what is our folder?
        File thisFolder = new File(folderCode, usernameTarget + "/" + repositoryName);
        // do we have the folder we need?
        utils.files.mkdirs(thisFolder);
        // now download the files
        core.rep.download(thisFolder, usernameTarget + "/" + repositoryName);
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

    
}