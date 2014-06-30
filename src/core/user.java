/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-06-30T14:07:13Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: user.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Methods related to github registered users</text> 
 */

package core;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.github.User;
import com.jcabi.github.wire.CarefulWire;
import static core.start.getLastLine;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nuno Brito, 30th of June 2014 in Darmstadt, Germany
 */
public class user {

    
      /**
     * Does the crawling of users at github. If a file already exists,
     * gets the last user and then processes from there forward.
     */
    static void launchCrawling() throws Exception{
        // check if our file already exists
        if(common.fileUsers.exists() == false){
            iterateUsers("");
            // no need to proceed
            return;
        }
        // get the last line of the text file
        String lastId = getLastLine(common.fileUsers);
        
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
        int idNumber = user.getUserIdNumber(lastId);
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
                new RtGithub(common.username, common.password)
                        .entry().through(CarefulWire.class, 50));
        Iterable<User> users = github.users().iterate(lastUser);
        // open up the text file with the logins
        BufferedWriter outLogin = new BufferedWriter(
                new FileWriter(common.filenameUsers, true), 8192);
        // iterate the users
        for (User user : users) {
            outLogin.write(user.login() + "\n");
            outLogin.flush();
            System.out.println(user.login());
        }
        
        System.out.println("All done!");
    }

    
    /**
     * Gets the user id from a given login name
     * @param loginName
     * @return the id number of a given login or -1 if the login was not found
     */
    public static int getUserIdNumber(String loginName) {
        Github github = new RtGithub(
                new RtGithub(common.username, common.password)
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
}
