/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-06-29T23:25:41Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: rep.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Function related to github repositories.</text> 
 */

package core;

import com.jcabi.github.Github;
import com.jcabi.github.RtGithub;
import com.jcabi.github.wire.CarefulWire;
import com.jcabi.http.response.JsonResponse;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import utils.files;

/**
 *
 * @author Nuno Brito, 29th of July 2014 in Darmstadt, Germany
 */
public class rep {
    
     
    /**
     * Launch the indexing of repositories available on github using a
     * previously indexed list of users.
     */
    public static void launchRepositoryIndexing() {
        // we need the list of users to be available
        if(common.fileUsers.exists() == false){
            System.err.println("Didn't found the index of users.");
            return;
        }
        
        // now go through each name on the user list to grab its repositories
        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(common.fileUsers));
            String line = "";
            while (line != null) {
                line = reader.readLine();
                // we don't process the line if it is null
                if(line != null){
                    // now get the repositories associated to this user
                    processRepositoriesFromUser(line);
                }
            }      
            // if there is an older archive, we should resume the operation
            //listRepositories("");
        } catch (IOException ex) {
            Logger.getLogger(start.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    
    
    /**
     * Get all the repositories registered on github. We need a user list
     * to be available for this purpose.
     */
    private static void listRepositories(final String lastRepository){
        System.out.println("All done!");
    }
    
    /**
     * Given a Github.com user, this method provides an arraylist
     * with all the respective repositories available
     * @param user
     * @return 
     */
    public static ArrayList<String> getRepositories(final String user){
        // create the new array
        ArrayList<String> result = new ArrayList();
        
        try{ 
           Github github = new RtGithub(
                new RtGithub(common.username, common.password)
                        .entry().through(CarefulWire.class, 50));          
           
           final JsonResponse resp = github.entry()
                .uri().path("/users/" + user + "/repos")
                //.queryParam("q", "java")
                .back()
                .fetch()
                .as(JsonResponse.class);
           
           // get the JSON reply
           final String answer = resp.json().read().toString();
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

               // now get the repository description
               final String description = getItem("description", item);

               // now get the repository description
               final String language = getItem("language", item);

               // create the output holder
               String output = name;
               
               // add the language designed by Github
               // shall we add a description?
               if(language.isEmpty()){
                   output += " none";
               }else{              
                   output += " " + language;
               }
               
               // shall we add a description?
               if(description.isEmpty() == false){
                   output += " " + description;
               }
               // all done
               result.add(output);
           }
           
           
//           // get the needed strings
//           Pattern pattern = Pattern.compile("(?<=(\\\"name\\\":\\\"))([a-zA-Z_-]+)");
//           Matcher matcher = pattern.matcher(answer);
//           while(matcher.find()) {
//               final String repositoryName = answer.substring(matcher.start(), matcher.end());
////               processRepository(user, repositoryName);
////               System.out.println("--> " + repositoryName);
//               if(hasValue(repositoryName, result) == false){
//                result.add(repositoryName);
//               }
//           }
           //System.out.println(resp.json().read().toString());
       }catch (IOException e){
           System.err.println(e.getMessage());
       }
        // all done
        return result;
    }
    
    
//    /**
//     * Checks if a given arraylist already contains a entry
//     * @param what  The entry to test
//     * @param array The array with all the entries
//     * @return  True if it has this value already. False otherwise.
//     */
//    static boolean hasValue(final String what, ArrayList<String> array){
//        boolean result = false;
//        for(final String thisValue : array){
//            if(text.equals(what, thisValue)){
//                result = true;
//                break;
//            }
//        }
//        return result;
//    }
    
    
    /**
     * Get the files from a given repository on github
     * @param localPath The folder on disk where the files will be placed
     * @param location The username/repository identification. We'd
     * expect something like triplecheck/reporter
     */
    public static void download(final File localPath, final String location){
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

        System.out.println("Having repository: " + repository.getDirectory());

        repository.close();
       
        } catch (IOException ex) {
            Logger.getLogger(rep.class.getName()).log(Level.SEVERE, null, ex);
        } catch (GitAPIException ex) {
            Logger.getLogger(rep.class.getName()).log(Level.SEVERE, null, ex);
        }
 
             System.out.println("--> " + location);
    }

    /**
     * This method will connect online to grab all the repositories associated
     * to a given user on github. The result will be listed to the console and
     * stored inside a text file.
     * @param targetUser 
     */
    private static void processRepositoriesFromUser(final String targetUser) {
        ArrayList<String> repositories = getRepositories(targetUser);
        for(final String repository : repositories){
            System.out.println(targetUser + "/" + repository);
        }
    }

     /**
     * Given a json snippet, we retrieve a value provided as parameter. There
     * is no need for the json to be correct, we accept partial snippets.
     * @param parameter The needle to find
     * @param content   The haystack where we'll look for the needle
     * @return          The value of the hay needle
     */
    private static String getItem(final String parameter, final String content){
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
    private static Boolean getItemBoolean(final String parameter, final String content){
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






/**
 Example of:
 * https://api.github.com/repos/evanphx/docker
 {
  "id": 12006301,
  "name": "docker",
  "full_name": "evanphx/docker",
  "owner": {
    "login": "evanphx",
    "id": 7,
    "avatar_url": "https://avatars.githubusercontent.com/u/7?",
    "gravatar_id": "540cb3b3712ffe045113cb03bab616a2",
    "url": "https://api.github.com/users/evanphx",
    "html_url": "https://github.com/evanphx",
    "followers_url": "https://api.github.com/users/evanphx/followers",
    "following_url": "https://api.github.com/users/evanphx/following{/other_user}",
    "gists_url": "https://api.github.com/users/evanphx/gists{/gist_id}",
    "starred_url": "https://api.github.com/users/evanphx/starred{/owner}{/repo}",
    "subscriptions_url": "https://api.github.com/users/evanphx/subscriptions",
    "organizations_url": "https://api.github.com/users/evanphx/orgs",
    "repos_url": "https://api.github.com/users/evanphx/repos",
    "events_url": "https://api.github.com/users/evanphx/events{/privacy}",
    "received_events_url": "https://api.github.com/users/evanphx/received_events",
    "type": "User",
    "site_admin": false
  },
  "private": false,
  "html_url": "https://github.com/evanphx/docker",
  "description": "Docker - the open-source application container engine",
  "fork": true,
  "url": "https://api.github.com/repos/evanphx/docker",
  "forks_url": "https://api.github.com/repos/evanphx/docker/forks",
  "keys_url": "https://api.github.com/repos/evanphx/docker/keys{/key_id}",
  "collaborators_url": "https://api.github.com/repos/evanphx/docker/collaborators{/collaborator}",
  "teams_url": "https://api.github.com/repos/evanphx/docker/teams",
  "hooks_url": "https://api.github.com/repos/evanphx/docker/hooks",
  "issue_events_url": "https://api.github.com/repos/evanphx/docker/issues/events{/number}",
  "events_url": "https://api.github.com/repos/evanphx/docker/events",
  "assignees_url": "https://api.github.com/repos/evanphx/docker/assignees{/user}",
  "branches_url": "https://api.github.com/repos/evanphx/docker/branches{/branch}",
  "tags_url": "https://api.github.com/repos/evanphx/docker/tags",
  "blobs_url": "https://api.github.com/repos/evanphx/docker/git/blobs{/sha}",
  "git_tags_url": "https://api.github.com/repos/evanphx/docker/git/tags{/sha}",
  "git_refs_url": "https://api.github.com/repos/evanphx/docker/git/refs{/sha}",
  "trees_url": "https://api.github.com/repos/evanphx/docker/git/trees{/sha}",
  "statuses_url": "https://api.github.com/repos/evanphx/docker/statuses/{sha}",
  "languages_url": "https://api.github.com/repos/evanphx/docker/languages",
  "stargazers_url": "https://api.github.com/repos/evanphx/docker/stargazers",
  "contributors_url": "https://api.github.com/repos/evanphx/docker/contributors",
  "subscribers_url": "https://api.github.com/repos/evanphx/docker/subscribers",
  "subscription_url": "https://api.github.com/repos/evanphx/docker/subscription",
  "commits_url": "https://api.github.com/repos/evanphx/docker/commits{/sha}",
  "git_commits_url": "https://api.github.com/repos/evanphx/docker/git/commits{/sha}",
  "comments_url": "https://api.github.com/repos/evanphx/docker/comments{/number}",
  "issue_comment_url": "https://api.github.com/repos/evanphx/docker/issues/comments/{number}",
  "contents_url": "https://api.github.com/repos/evanphx/docker/contents/{+path}",
  "compare_url": "https://api.github.com/repos/evanphx/docker/compare/{base}...{head}",
  "merges_url": "https://api.github.com/repos/evanphx/docker/merges",
  "archive_url": "https://api.github.com/repos/evanphx/docker/{archive_format}{/ref}",
  "downloads_url": "https://api.github.com/repos/evanphx/docker/downloads",
  "issues_url": "https://api.github.com/repos/evanphx/docker/issues{/number}",
  "pulls_url": "https://api.github.com/repos/evanphx/docker/pulls{/number}",
  "milestones_url": "https://api.github.com/repos/evanphx/docker/milestones{/number}",
  "notifications_url": "https://api.github.com/repos/evanphx/docker/notifications{?since,all,participating}",
  "labels_url": "https://api.github.com/repos/evanphx/docker/labels{/name}",
  "releases_url": "https://api.github.com/repos/evanphx/docker/releases{/id}",
  "created_at": "2013-08-09T17:10:08Z",
  "updated_at": "2013-08-30T00:02:33Z",
  "pushed_at": "2013-08-18T03:04:03Z",
  "git_url": "git://github.com/evanphx/docker.git",
  "ssh_url": "git@github.com:evanphx/docker.git",
  "clone_url": "https://github.com/evanphx/docker.git",
  "svn_url": "https://github.com/evanphx/docker",
  "homepage": "http://www.docker.io",
  "size": 11238,
  "stargazers_count": 0,
  "watchers_count": 0,
  "language": "Go",
  "has_issues": false,
  "has_downloads": true,
  "has_wiki": true,
  "forks_count": 0,
  "mirror_url": null,
  "open_issues_count": 0,
  "forks": 0,
  "open_issues": 0,
  "watchers": 0,
  "default_branch": "master",
  "parent": {
    "id": 7691631,
    "name": "docker",
    "full_name": "dotcloud/docker",
    "owner": {
      "login": "dotcloud",
      "id": 171922,
      "avatar_url": "https://avatars.githubusercontent.com/u/171922?",
      "gravatar_id": "d2fbb59e6c8b80a26e48b06ea30d53fd",
      "url": "https://api.github.com/users/dotcloud",
      "html_url": "https://github.com/dotcloud",
      "followers_url": "https://api.github.com/users/dotcloud/followers",
      "following_url": "https://api.github.com/users/dotcloud/following{/other_user}",
      "gists_url": "https://api.github.com/users/dotcloud/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/dotcloud/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/dotcloud/subscriptions",
      "organizations_url": "https://api.github.com/users/dotcloud/orgs",
      "repos_url": "https://api.github.com/users/dotcloud/repos",
      "events_url": "https://api.github.com/users/dotcloud/events{/privacy}",
      "received_events_url": "https://api.github.com/users/dotcloud/received_events",
      "type": "Organization",
      "site_admin": false
    },
    "private": false,
    "html_url": "https://github.com/dotcloud/docker",
    "description": "Docker - the open-source application container engine",
    "fork": false,
    "url": "https://api.github.com/repos/dotcloud/docker",
    "forks_url": "https://api.github.com/repos/dotcloud/docker/forks",
    "keys_url": "https://api.github.com/repos/dotcloud/docker/keys{/key_id}",
    "collaborators_url": "https://api.github.com/repos/dotcloud/docker/collaborators{/collaborator}",
    "teams_url": "https://api.github.com/repos/dotcloud/docker/teams",
    "hooks_url": "https://api.github.com/repos/dotcloud/docker/hooks",
    "issue_events_url": "https://api.github.com/repos/dotcloud/docker/issues/events{/number}",
    "events_url": "https://api.github.com/repos/dotcloud/docker/events",
    "assignees_url": "https://api.github.com/repos/dotcloud/docker/assignees{/user}",
    "branches_url": "https://api.github.com/repos/dotcloud/docker/branches{/branch}",
    "tags_url": "https://api.github.com/repos/dotcloud/docker/tags",
    "blobs_url": "https://api.github.com/repos/dotcloud/docker/git/blobs{/sha}",
    "git_tags_url": "https://api.github.com/repos/dotcloud/docker/git/tags{/sha}",
    "git_refs_url": "https://api.github.com/repos/dotcloud/docker/git/refs{/sha}",
    "trees_url": "https://api.github.com/repos/dotcloud/docker/git/trees{/sha}",
    "statuses_url": "https://api.github.com/repos/dotcloud/docker/statuses/{sha}",
    "languages_url": "https://api.github.com/repos/dotcloud/docker/languages",
    "stargazers_url": "https://api.github.com/repos/dotcloud/docker/stargazers",
    "contributors_url": "https://api.github.com/repos/dotcloud/docker/contributors",
    "subscribers_url": "https://api.github.com/repos/dotcloud/docker/subscribers",
    "subscription_url": "https://api.github.com/repos/dotcloud/docker/subscription",
    "commits_url": "https://api.github.com/repos/dotcloud/docker/commits{/sha}",
    "git_commits_url": "https://api.github.com/repos/dotcloud/docker/git/commits{/sha}",
    "comments_url": "https://api.github.com/repos/dotcloud/docker/comments{/number}",
    "issue_comment_url": "https://api.github.com/repos/dotcloud/docker/issues/comments/{number}",
    "contents_url": "https://api.github.com/repos/dotcloud/docker/contents/{+path}",
    "compare_url": "https://api.github.com/repos/dotcloud/docker/compare/{base}...{head}",
    "merges_url": "https://api.github.com/repos/dotcloud/docker/merges",
    "archive_url": "https://api.github.com/repos/dotcloud/docker/{archive_format}{/ref}",
    "downloads_url": "https://api.github.com/repos/dotcloud/docker/downloads",
    "issues_url": "https://api.github.com/repos/dotcloud/docker/issues{/number}",
    "pulls_url": "https://api.github.com/repos/dotcloud/docker/pulls{/number}",
    "milestones_url": "https://api.github.com/repos/dotcloud/docker/milestones{/number}",
    "notifications_url": "https://api.github.com/repos/dotcloud/docker/notifications{?since,all,participating}",
    "labels_url": "https://api.github.com/repos/dotcloud/docker/labels{/name}",
    "releases_url": "https://api.github.com/repos/dotcloud/docker/releases{/id}",
    "created_at": "2013-01-18T18:10:57Z",
    "updated_at": "2014-06-30T08:32:15Z",
    "pushed_at": "2014-06-30T12:21:58Z",
    "git_url": "git://github.com/dotcloud/docker.git",
    "ssh_url": "git@github.com:dotcloud/docker.git",
    "clone_url": "https://github.com/dotcloud/docker.git",
    "svn_url": "https://github.com/dotcloud/docker",
    "homepage": "http://www.docker.com",
    "size": 113753,
    "stargazers_count": 13272,
    "watchers_count": 13272,
    "language": "Go",
    "has_issues": true,
    "has_downloads": true,
    "has_wiki": true,
    "forks_count": 2322,
    "mirror_url": null,
    "open_issues_count": 656,
    "forks": 2322,
    "open_issues": 656,
    "watchers": 13272,
    "default_branch": "master"
  },
  "source": {
    "id": 7691631,
    "name": "docker",
    "full_name": "dotcloud/docker",
    "owner": {
      "login": "dotcloud",
      "id": 171922,
      "avatar_url": "https://avatars.githubusercontent.com/u/171922?",
      "gravatar_id": "d2fbb59e6c8b80a26e48b06ea30d53fd",
      "url": "https://api.github.com/users/dotcloud",
      "html_url": "https://github.com/dotcloud",
      "followers_url": "https://api.github.com/users/dotcloud/followers",
      "following_url": "https://api.github.com/users/dotcloud/following{/other_user}",
      "gists_url": "https://api.github.com/users/dotcloud/gists{/gist_id}",
      "starred_url": "https://api.github.com/users/dotcloud/starred{/owner}{/repo}",
      "subscriptions_url": "https://api.github.com/users/dotcloud/subscriptions",
      "organizations_url": "https://api.github.com/users/dotcloud/orgs",
      "repos_url": "https://api.github.com/users/dotcloud/repos",
      "events_url": "https://api.github.com/users/dotcloud/events{/privacy}",
      "received_events_url": "https://api.github.com/users/dotcloud/received_events",
      "type": "Organization",
      "site_admin": false
    },
    "private": false,
    "html_url": "https://github.com/dotcloud/docker",
    "description": "Docker - the open-source application container engine",
    "fork": false,
    "url": "https://api.github.com/repos/dotcloud/docker",
    "forks_url": "https://api.github.com/repos/dotcloud/docker/forks",
    "keys_url": "https://api.github.com/repos/dotcloud/docker/keys{/key_id}",
    "collaborators_url": "https://api.github.com/repos/dotcloud/docker/collaborators{/collaborator}",
    "teams_url": "https://api.github.com/repos/dotcloud/docker/teams",
    "hooks_url": "https://api.github.com/repos/dotcloud/docker/hooks",
    "issue_events_url": "https://api.github.com/repos/dotcloud/docker/issues/events{/number}",
    "events_url": "https://api.github.com/repos/dotcloud/docker/events",
    "assignees_url": "https://api.github.com/repos/dotcloud/docker/assignees{/user}",
    "branches_url": "https://api.github.com/repos/dotcloud/docker/branches{/branch}",
    "tags_url": "https://api.github.com/repos/dotcloud/docker/tags",
    "blobs_url": "https://api.github.com/repos/dotcloud/docker/git/blobs{/sha}",
    "git_tags_url": "https://api.github.com/repos/dotcloud/docker/git/tags{/sha}",
    "git_refs_url": "https://api.github.com/repos/dotcloud/docker/git/refs{/sha}",
    "trees_url": "https://api.github.com/repos/dotcloud/docker/git/trees{/sha}",
    "statuses_url": "https://api.github.com/repos/dotcloud/docker/statuses/{sha}",
    "languages_url": "https://api.github.com/repos/dotcloud/docker/languages",
    "stargazers_url": "https://api.github.com/repos/dotcloud/docker/stargazers",
    "contributors_url": "https://api.github.com/repos/dotcloud/docker/contributors",
    "subscribers_url": "https://api.github.com/repos/dotcloud/docker/subscribers",
    "subscription_url": "https://api.github.com/repos/dotcloud/docker/subscription",
    "commits_url": "https://api.github.com/repos/dotcloud/docker/commits{/sha}",
    "git_commits_url": "https://api.github.com/repos/dotcloud/docker/git/commits{/sha}",
    "comments_url": "https://api.github.com/repos/dotcloud/docker/comments{/number}",
    "issue_comment_url": "https://api.github.com/repos/dotcloud/docker/issues/comments/{number}",
    "contents_url": "https://api.github.com/repos/dotcloud/docker/contents/{+path}",
    "compare_url": "https://api.github.com/repos/dotcloud/docker/compare/{base}...{head}",
    "merges_url": "https://api.github.com/repos/dotcloud/docker/merges",
    "archive_url": "https://api.github.com/repos/dotcloud/docker/{archive_format}{/ref}",
    "downloads_url": "https://api.github.com/repos/dotcloud/docker/downloads",
    "issues_url": "https://api.github.com/repos/dotcloud/docker/issues{/number}",
    "pulls_url": "https://api.github.com/repos/dotcloud/docker/pulls{/number}",
    "milestones_url": "https://api.github.com/repos/dotcloud/docker/milestones{/number}",
    "notifications_url": "https://api.github.com/repos/dotcloud/docker/notifications{?since,all,participating}",
    "labels_url": "https://api.github.com/repos/dotcloud/docker/labels{/name}",
    "releases_url": "https://api.github.com/repos/dotcloud/docker/releases{/id}",
    "created_at": "2013-01-18T18:10:57Z",
    "updated_at": "2014-06-30T08:32:15Z",
    "pushed_at": "2014-06-30T12:21:58Z",
    "git_url": "git://github.com/dotcloud/docker.git",
    "ssh_url": "git@github.com:dotcloud/docker.git",
    "clone_url": "https://github.com/dotcloud/docker.git",
    "svn_url": "https://github.com/dotcloud/docker",
    "homepage": "http://www.docker.com",
    "size": 113753,
    "stargazers_count": 13272,
    "watchers_count": 13272,
    "language": "Go",
    "has_issues": true,
    "has_downloads": true,
    "has_wiki": true,
    "forks_count": 2322,
    "mirror_url": null,
    "open_issues_count": 656,
    "forks": 2322,
    "open_issues": 656,
    "watchers": 13272,
    "default_branch": "master"
  },
  "network_count": 2322,
  "subscribers_count": 1
}

 
 
 
 */