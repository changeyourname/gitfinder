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
import com.jcabi.github.RtGithub;
import com.jcabi.github.User;
import com.jcabi.github.wire.CarefulWire;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

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
    
    /**
     * @param args the command line arguments
     * @throws java.lang.Exception
     */
    public static void main(String[] args) throws Exception {
                
        setLoginDetails(args);
        
        // are we looking to index the users?
        if(args[0].equalsIgnoreCase("users")){
            System.out.println("Indexing the users");
            launchCrawling();
            return;
        }
        
        // or are we interested in indexing repositories?
        if(args[0].equalsIgnoreCase("repositories")){
            System.out.println("Indexing the repositories");
            return;
        }
        
        
        // no arguments specified, show a simple syntax usage
        System.out.println("Syntax usage: java -jar gitfinder.java users|repositories username password"
                + "\n"
                + "Example:\n"
                + "java -jar gitfinder.jar repositories mylogin mypassword");
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
        
        // a file exists, let's try to get the last user name mentioned
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = "";
        String lastId = "";
            while (line != null) {
                line = reader.readLine();
                if(line != null){
                    lastId = line;
                }
                // we don't care about the content, just move to the last line
            }

        // looks good so far. Did we got a user name?    
        if(lastId.isEmpty()){
            // was empty, let's start again from scratch
            iterateUsers("");
            // no need to proceed
            return;
        }    
            
        System.out.println("Resuming the index since username: " + lastId);
        
        // now get the ID number for this user
        int idNumber = getUserIdNumber(lastId);
            
        System.out.println("Last login ID: " + idNumber);
        
        if(idNumber == -1){
            System.err.println("Failed to get the Id number for " + lastId);
            return;
        }
        
        // ok, ready to keep iterating through new users
       iterateUsers(idNumber + "");

    }
    
    
    /**
     * Get all the users registered on github
     */
    static void iterateUsers(final String lastUser) throws Exception{
        //Github github = new RtGithub(username, password).entry().through(CarefulWire.class, 50);
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
        
        System.out.println("All done!"
                + "");
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
    private static void setLoginDetails(String[] args) {
        // initialize the preferences object
        prefs = Preferences.userNodeForPackage(start.class);
        // do we have any login details specified?
        if(args.length < 2){
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
            
            
            return;
        }
        
        
        // we have some values, expect them to be the login parameters
        password = args[2];
        username = args[1];
        // now save these into our preferences object
        prefs.put("username", username);
        prefs.put("password", password);
        
    }
}