/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-06-29T23:25:41Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: Repositories.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Function related to github repositories.</text> 
 */

package main;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.github.wire.CarefulWire;
import com.jcabi.http.response.JsonResponse;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import structure.Rep;
import utils.files;

/**
 *
 * @author Nuno Brito, 29th of June 2014 in Darmstadt, Germany
 */
public class Repositories {
        
    BufferedReader reader;
    BufferedWriter writer;    
     
    // are we ready to start working?
    boolean hasInitialized = false;
        
    
    public Repositories(){
        doSettings();
    }
     
    /**
     * Prepare the files for editing
     */
    private void doSettings(){
         try {
             // sometimes we don't have yet a users.txt file
            if(core.fileUsers.exists() == false){
                // time to create an empty one
                utils.files.touch(core.fileUsers);
            } 
             // open the users' file for reading
            reader = new BufferedReader(new FileReader(core.fileUsers));
            // initialize the text file where the repositories are written
            writer = new BufferedWriter(
                new FileWriter(core.fileRepositories, true), 8192);
            // are we resuming a previous operation?
            if(core.fileRepositories.exists()){
                // now get the last Id that was written to disk
                final String lastId = getLastIndexedId();
                // time to speed up until we find it again on the user list
                String lastIndexedId = "";
                // now go and try to find the respective user id
                while (lastIndexedId != null) {
                    lastIndexedId = reader.readLine();
                    // when we find a match, break here
                    if(utils.text.equals(lastIndexedId, lastId)){
                        break;
                    }
                }
                // check if we did found the id
                if(lastIndexedId == null){
                    System.err.println("Error: Didn't found this Id: " + lastId);
                    hasInitialized = false;
                    return;
                }
            }
            
            
            } catch (IOException ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
            hasInitialized = false;
        }
         // all systems ready to go
         hasInitialized = true;
    }
        
    /**
     * Launch the indexing of repositories available on github using a
     * previously indexed list of users.
     */
    public void launchRepositoryIndexing() {
        // we need the list of users to be available
        if(core.fileUsers.exists() == false){
            System.err.println("Error: Didn't found the index of users.");
            return;
        }
         // are we good to go?
        if(hasInitialized == false){
            System.err.println("Error: Repositories are not initialized");
            return;
        }
            
        String nextUser = "";
        while (nextUser != null) {
            nextUser = getNextUser();
            // we don't process the line if it is null
            if(nextUser != null){
                // now get the repositories associated to this user
                String result = getLinesWithRepositoriesFromUser(nextUser);
                // ok, all done. Add up the new info!
                addNewRepositories(result);
            }
        }      
    }
    
    /**
     * Gets the next user to have his repositories analysed
     * @return The next user name to be indexed, or null if none was found
     */
    public String getNextUser(){
        try {
            return reader.readLine();
        } catch (IOException ex) {
            Logger.getLogger(Repositories.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    /**
     * This method will look into repositories.txt to first find the last
     * line that was added on the file and then extract the respective username.
     * @return The user name of the last repository that was written on the file
     */
    public String getLastIndexedId(){
        // maybe we should skip onto the last line that was read
        final String lastLine = core.getLastLine(core.fileRepositories);
        int i1 = lastLine.indexOf("/");
        // we need to have a slash, otherwise things should fail here
        if(i1 < 0){
            System.err.println("Error: Last line of repository text file was not valid.");
            return null;
        }
        // now get the last Id that was written to disk
        return lastLine.substring(0, i1);
    }
    
    
    /**
     * Given a Github.com user, this method provides an arraylist
     * with all the respective repositories available
     * @param user
     * @return 
     */
    public ArrayList<Rep> getRepositories(final String user){
        // create the new array
        ArrayList<Rep> result = new ArrayList();
        
        try{ 
           Github github = new RtGithub(
                new RtGithub(
                   core.username, core.password
           )
                        .entry().through(CarefulWire.class, 50));
            //  .entry());
           
           final JsonResponse resp = github.entry()
                .uri().path("/users/" + user + "/repos")
                //.queryParam("q", "java")
                .back()
                .fetch()
                .as(JsonResponse.class);
           
           // get the JSON reply
           final String answer = resp.json().read().toString();
           
           // something wrong happened here
           if(answer.length() < 25){
               System.out.println("RP188 - ERROR, Answer from github rep was too short:"
                       + "" + answer);
               return null;
           }
            // split the repo message into different items
           String[] items = answer.split("default_branch\":");
           // now iterate each one
           for(final String item : items){
               // get the name for this item
               final String name = getItem("name", item);
               
               // no need to proceed if the string has no relevant data
               if(name.isEmpty()){
                   continue;
               }
               // Was this repository forked from somewhere?
               final boolean fork = getItemBoolean("fork", item);

               // we only care about non-forked projects
               if(fork){
                   continue;
               }

               // create the repository object
               Rep rep = new Rep();
               rep.setIdUser(user);
               rep.setIdRepository(name);
               
               // now get the repository description
               final String description = getItem("description", item);
               // now get the repository description
               final String language = getItem("language", item);

               // add the language designed by Github
               // shall we add a description?
               if(language.isEmpty()){
                   rep.setLanguage("none");
               }else{              
                   rep.setLanguage(language);
               }
               
               // shall we add a description?
               if(description.isEmpty() == false){
                   rep.setDescription(description);
               }
               // all done
               result.add(rep);
           }
     
       }catch (IOException e){
           System.err.println("Error occurred in " + utils.time.getDateTime());
           System.err.println(e.getMessage());
           result = null;
       }
        // all done
        return result;
    }
    
    
    /**
     * Get the files from a given repository on github
     * @param localPath The folder on disk where the files will be placed
     * @param location The username/repository identification. We'd
     * expect something like triplecheck/reporter
     */
    public void download(final File localPath, final String location){
        // we can't have any older files
        files.deleteDir(localPath);
        final String REMOTE_URL = "https://github.com/"+location+".git";
        try {
            
            Git.cloneRepository()
                    .setURI(REMOTE_URL)
                    .setDirectory(localPath)
                    .call();
       
        // now open the created repository
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        Repository repository = builder.setGitDir(localPath)
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();

        System.out.println("Downloaded repository: " + repository.getDirectory());

        repository.close();
       
        } catch (IOException ex) {
            Logger.getLogger(Repositories.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GitAPIException ex) {
            Logger.getLogger(Repositories.class.getName()).log(Level.SEVERE, null, ex);
        }
 
             System.out.println("--> " + location);
    }

    /**
     * This method will connect online to grab all the repositories associated
     * to a given user on github. The result will be listed to the console and
     * stored inside a text file.
     * @param targetUser 
     * @return  the lines to be added on our repository text file
     */
    public String getLinesWithRepositoriesFromUser(final String targetUser) {
        ArrayList<Rep> repositories = getRepositories(targetUser);
        String lines = "";
        // iterate each repository found
        for(final Rep rep : repositories){
            // prepare the output
            final String line = rep.getOneline();
            System.out.println(line);
            // add another line
            lines = lines.concat(line+"\n");
         }
        return lines;
    }

    /**
     * Write a given number of lines onto our repository text file. There is
     * no checking about the quality of the content. You are responsible to
     * ensure that the lines are properly formed. Typically, it is expected
     * that each line is concluded with an "\n" and that it starts by the useID,
     * followed by the repository id, concluded language and then comments.
     * Below is an example:
     * triplecheck/download Java A repository for downloading files\n
     * @param lines 
     */
    public void addNewRepositories(final String lines){
        // now write all the lines in a single push
        try {
                writer.write(lines);
                writer.flush();
        } catch (IOException ex) {
            Logger.getLogger(Repositories.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
     /**
     * Given a json snippet, we retrieve a value provided as parameter. There
     * is no need for the json to be correct, we accept partial snippets.
     * @param parameter The needle to find
     * @param content   The haystack where we'll look for the needle
     * @return          The value of the hay needle
     */
    private String getItem(final String parameter, final String content){
        String result;
        // construct the keyword detection
        final String keyword = 
                  "\""
                + parameter
                + "\":\"";
        // now get the anchor position
        final int i1 = content.indexOf(keyword);
        // have we got a result?
        if(i1 < 0){
            return "";
        }
        // get the respective string
        final String line = content.substring(i1 + keyword.length());
        // get the position of the last "
        final int i2 = line.indexOf("\"");
        // finally get the end result
        result = line.substring(0, i2);
        return result;
    }

    
   /**
     * Given a json snippet, we retrieve a value provided as parameter. There
     * is no need for the json to be correct, we accept partial snippets.
     * @param parameter The needle to find
     * @param content   The haystack where we'll look for the needle
     * @return          The value of the hay needle
     */
    private Boolean getItemBoolean(final String parameter, final String content){
        Boolean result;
        // construct the keyword detection
        final String keyword = 
                  "\""
                + parameter
                + "\":";
        // now get the anchor position
        final int i1 = content.indexOf(keyword);
        // have we got a result?
        if(i1 < 0){
            //System.err.println("Didn't found: " + parameter + " in " + content);
            return false;
        }
        // get the respective string
        final String line = content.substring(i1 + keyword.length());
        // get the position of the last "
        final int i2 = line.indexOf(",");
        // finally get the end result
        result = Boolean.valueOf(line.substring(0, i2));
        //System.out.println(line.substring(0, i2));
        return result;
    }  
    
    
}
