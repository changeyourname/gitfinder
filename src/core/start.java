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

package core;

import com.jcabi.github.Github;
import com.jcabi.github.Repo;
import com.jcabi.github.Repos;
import com.jcabi.github.RtGithub;
import com.jcabi.github.User;
import com.jcabi.github.wire.CarefulWire;
import com.jcabi.http.response.JsonResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;
import javax.json.JsonObject;

/**
 *
 * @author Nuno Brito, 28th of May 2014 in Paris, France.
 */
public class start {

    static String username = "";
    static String password = "";
    static Preferences prefs;
    final static String filenameUsers = "users.txt";
    final static String filenameRepositories = "repositories.txt";
    
    // the root folder where all code ends up stored
    final static File folderCode = new File("./code");
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {

        // ensure that we have the needed credentials for github
        setLoginDetails();
        
        // are we looking to index the users?
        if(args[0].equalsIgnoreCase("users")){
            System.out.println("Indexing the users");
            launchCrawling();
            return;
        }
        
        // or are we interested in indexing repositories?
        if(args[0].equalsIgnoreCase("repositories")){
            setLoginDetails();
            System.out.println("Indexing repositories");
            launchRepositoryIndexing();
            return;
        }
        
               
        // or are we interested in grabbing files from repositories?
        if(args[0].equalsIgnoreCase("grab")){
            System.out.println("Grabbing files");
            launchGrabFiles(args[1]);
            return;
        }

               
        // or are we interested in grabbing files from repositories?
        if(args[0].equalsIgnoreCase("reset")){
            System.out.println("Resetting user details");
            prefs.remove("username");
            prefs.remove("password");
            System.out.println("Done");
            return;
        }

        
        
        
        // no arguments specified, show a simple syntax usage
        System.out.println("Syntax usage: java -jar gitfinder.java users|repositories username password"
                + "\n"
                + "Example:\n"
                + "java -jar gitfinder.jar repositories mylogin mypassword");
    }
    
    /**
     * Returns the last line from a given text file
     * @param file  A file on disk 
     * @return The last line if available or an empty string if nothing
     * was found
     */
    static String getLastLine(File file){
        String result = "";
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(file));
       
         String line = "";
            while (line != null) {
                line = reader.readLine();
                if(line != null){
                    result = line;
                }
                // we don't care about the content, just move to the last line
            }
        
        } catch (IOException ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
        }
      
        // all done    
        return result;
    }
    
    /**
     * Does the crawling of users at github. If a file already exists,
     * gets the last user and then processes from there forward.
     */
    static void launchCrawling() throws Exception{
        // check if our file already exists
        File file = new File(filenameUsers);
        // file doesn't exist, start from scratch
        if(file.exists() == false){
            iterateUsers("");
            // no need to proceed
            return;
        }
        // get the last line of the text file
        String lastId = getLastLine(file);
        
        // looks good so far. Did we got a user name?    
        if(lastId.isEmpty()){
            // was empty, let's start again from scratch
            iterateUsers("");
            // no need to proceed
            return;
        }    
            
        // good to resume operations
        System.out.println("Resuming the index since username: " + lastId);
        
        // now get the ID number for this user
        int idNumber = getUserIdNumber(lastId);
        System.out.println("Last login ID: " + idNumber);
        
        if(idNumber == -1){
            System.err.println("Failed to get the Id number for " + lastId);
            return;
        }
        
        // ok, ready to continue iterating through new users
       iterateUsers(idNumber + "");
    }
    
    
    /**
     * Get all the users registered on github
     */
    static void iterateUsers(final String lastUser) throws Exception{
        // the github object
        Github github = new RtGithub(
                new RtGithub(username, password)
                        .entry().through(CarefulWire.class, 50));
        Iterable<User> users = github.users().iterate(lastUser);
        // open up the text file with the logins
        BufferedWriter outLogin = new BufferedWriter(
                new FileWriter(filenameUsers, true), 8192);
        // iterate the users
        for (User user : users) {
            outLogin.write(user.login() + "\n");
            outLogin.flush();
            System.out.println(user.login());
        }
        
        System.out.println("All done!");
    }

    /**
     * Get all the users registered on github
     */
    static void iterateRepositories(final String lastRepository) throws Exception{
       // the github object
        Github github = new RtGithub(
                new RtGithub(username, password)
                        .entry().through(CarefulWire.class, 50));
        // now go through each repository available
       
//        
//        Iterable<Repos> repos = github.repos().iterate(lastRepository);
//                
//                
//        // open up the text file with the logins
//        BufferedWriter outLogin = new BufferedWriter(
//                new FileWriter(filenameUsers, true), 8192);
//        // iterate the users
//        for (User user : users) {
//            outLogin.write(user.login() + "\n");
//            outLogin.flush();
//            System.out.println(user.login());
//        }
        
        System.out.println("All done!");
    }
   
    
    
    private static void launchRepositoryIndexing() {
        try {
            // if there is an older archive, we should resume the operation
            
            
            iterateRepositories("");
            
            
        } catch (Exception ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Gets the user id from a given login name
     * @param loginName
     */
    private static int getUserIdNumber(String loginName) {
        Github github = new RtGithub(
                new RtGithub(username, password)
                        .entry().through(CarefulWire.class, 50));
        // get the user object associated with a given id
        User user = github.users().get(loginName);

        try {
            /**
             * {"login":"pvcshell46",
             * "id":3324839,
             * "avatar_url":"https://avatars.githubusercontent.com/u/3324839?",
             * "gravatar_id":"0773d56e403664e0d1abe51c27b740f5",
             * "url":"https://api.github.com/users/pvcshell46",
             * "html_url":"https://github.com/pvcshell46",
             * "followers_url":"https://api.github.com/users/pvcshell46/followers",
             * "following_url":"https://api.github.com/users/pvcshell46/following{/other_user}",
             * "gists_url":"https://api.github.com/users/pvcshell46/gists{/gist_id}",
             * "starred_url":"https://api.github.com/users/pvcshell46/starred{/owner}{/repo}",
             * "subscriptions_url":"https://api.github.com/users/pvcshell46/subscriptions",
             * "organizations_url":"https://api.github.com/users/pvcshell46/orgs",
             * "repos_url":"https://api.github.com/users/pvcshell46/repos",
             * "events_url":"https://api.github.com/users/pvcshell46/events{/privacy}",
             * "received_events_url":"https://api.github.com/users/pvcshell46/received_events",
             * "type":"User",
             * "site_admin":false,
             * "name":null,
             * "company":null,
             * "blog":null,
             * "location":null,
             * "email":null,
             * "hireable":false,
             * "bio":"",
             * "public_repos":0,
             * "public_gists":0,
             * "followers":0,
             * "following":0,
             * "created_at":"2013-01-21T03:05:33Z",
             * "updated_at":"2013-02-26T22:39:17Z"}
            
            * 
            */
            //System.out.println(user.json().toString());
            return user.json().getInt("id");
            
        } catch (IOException ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return -1;
    }

    /**
     * Sets the login details that will be used with the github API
     * @param args 
     */
    private static void setLoginDetails() {
        // initialize the preferences object
        prefs = Preferences.userNodeForPackage(start.class);
        // do we have any login details specified?
//        if(args.length < 2){
            // get the values from save preferences if any
            username = prefs.get("username", "");
            password = prefs.get("password", "");
            
            // no need to continue if they were already set
            if(username.isEmpty() == false && password.isEmpty() == false){
                System.out.println("Using the login details of " + username);
                return;
            }
            
            
            System.out.println("Attention: No username nor password provided");
            System.out.println("In order to use the Github API in full speed, you need to provide a github login/password\n");
            
            
            try {
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
            
                System.out.println("Your github login name: ");
                username = in.readLine();
                    
                System.out.println("Your github password: ");
                password = in.readLine();

                // now save these into our preferences object
                prefs.put("username", username);
                prefs.put("password", password);
            } catch (IOException ex) {
                Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
            }
      
    }

    /**
     * When given a user name, go after the repositories where the user
     * is involved and grab a copy
     */
    private static void launchGrabFiles(final String usernameTarget) {
        if(usernameTarget == null){
            System.out.println("You need to specify a user name");
            return;
        }
        // we have a user name, let's start
        System.out.println("Processing " + usernameTarget);
        
        // what is our folder?
        File thisFolder = new File(folderCode, usernameTarget);
        
        // do we have the folder we need?
        utils.files.mkdirs(thisFolder);
        
//        // with the user name, grab the object from Github
//         Github github = new RtGithub(
//                new RtGithub()
//                //new RtGithub(username, password)
//                        .entry().through(CarefulWire.class, 50));
//        // get the user object associated with a given id
//        User user = github.users().get("esa");
//        
//        try {
//           
//           Repos repos = github.repos();
//           
//           Repo repo = repos.get(null);
//           
//          
//            
//        } catch (Exception ex) {
//            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
       try{ 
        
//           final Github github = new RtGithub();
          
           Github github = new RtGithub(
//                new RtGithub()
                new RtGithub(username, password)
                        .entry().through(CarefulWire.class, 50));          
           
           
           
           final JsonResponse resp = github.entry()
                .uri().path("/users/esa/repos")
                //.queryParam("q", "java")
                .back()
                .fetch()
                .as(JsonResponse.class);
           
           System.out.println(resp.json().read().toString());
           
//            final List<JsonObject> items = resp.json().readObject()
//                .getJsonArray("items")
//                .getValuesAs(JsonObject.class);
//            for (final JsonObject item : items) {
//                System.out.println(
//                    String.format(
//                        "repository found: %s",
//                        item.get("full_name").toString()
//                    )
//                );
//            }
        
       }catch (Exception e){
           e.printStackTrace();
       }
        
    }

    
}