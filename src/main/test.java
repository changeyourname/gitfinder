/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-06-30T14:42:41Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: test.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Helps to test some code snippets</text> 
 */

package main;


/**
 *
 * @author Nuno Brito, 30th of June 2014 in Darmstadt, Germany
 */
public class test {

    

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
                + "\": \"";
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
                + "\": ";
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
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // split the repo message into different items
       String[] items = textTest.split("\"default_branch\": \"master\"");
       // now iterate each one
       for(final String item : items){
          
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
           
           System.out.println(name 
                   + " -> "
                   + description
           );
       }
    }
    
    
    
    static final String textTest = "[\n" +
"  {\n" +
"    \"id\": 11374954,\n" +
"    \"name\": \"ace\",\n" +
"    \"full_name\": \"evanphx/ace\",\n" +
"    \"owner\": {\n" +
"      \"login\": \"evanphx\",\n" +
"      \"id\": 7,\n" +
"      \"avatar_url\": \"https://avatars.githubusercontent.com/u/7?\",\n" +
"      \"gravatar_id\": \"540cb3b3712ffe045113cb03bab616a2\",\n" +
"      \"url\": \"https://api.github.com/users/evanphx\",\n" +
"      \"html_url\": \"https://github.com/evanphx\",\n" +
"      \"followers_url\": \"https://api.github.com/users/evanphx/followers\",\n" +
"      \"following_url\": \"https://api.github.com/users/evanphx/following{/other_user}\",\n" +
"      \"gists_url\": \"https://api.github.com/users/evanphx/gists{/gist_id}\",\n" +
"      \"starred_url\": \"https://api.github.com/users/evanphx/starred{/owner}{/repo}\",\n" +
"      \"subscriptions_url\": \"https://api.github.com/users/evanphx/subscriptions\",\n" +
"      \"organizations_url\": \"https://api.github.com/users/evanphx/orgs\",\n" +
"      \"repos_url\": \"https://api.github.com/users/evanphx/repos\",\n" +
"      \"events_url\": \"https://api.github.com/users/evanphx/events{/privacy}\",\n" +
"      \"received_events_url\": \"https://api.github.com/users/evanphx/received_events\",\n" +
"      \"type\": \"User\",\n" +
"      \"site_admin\": false\n" +
"    },\n" +
"    \"private\": false,\n" +
"    \"html_url\": \"https://github.com/evanphx/ace\",\n" +
"    \"description\": \"Ace (Ajax.org Cloud9 Editor)\",\n" +
"    \"fork\": true,\n" +
"    \"url\": \"https://api.github.com/repos/evanphx/ace\",\n" +
"    \"forks_url\": \"https://api.github.com/repos/evanphx/ace/forks\",\n" +
"    \"keys_url\": \"https://api.github.com/repos/evanphx/ace/keys{/key_id}\",\n" +
"    \"collaborators_url\": \"https://api.github.com/repos/evanphx/ace/collaborators{/collaborator}\",\n" +
"    \"teams_url\": \"https://api.github.com/repos/evanphx/ace/teams\",\n" +
"    \"hooks_url\": \"https://api.github.com/repos/evanphx/ace/hooks\",\n" +
"    \"issue_events_url\": \"https://api.github.com/repos/evanphx/ace/issues/events{/number}\",\n" +
"    \"events_url\": \"https://api.github.com/repos/evanphx/ace/events\",\n" +
"    \"assignees_url\": \"https://api.github.com/repos/evanphx/ace/assignees{/user}\",\n" +
"    \"branches_url\": \"https://api.github.com/repos/evanphx/ace/branches{/branch}\",\n" +
"    \"tags_url\": \"https://api.github.com/repos/evanphx/ace/tags\",\n" +
"    \"blobs_url\": \"https://api.github.com/repos/evanphx/ace/git/blobs{/sha}\",\n" +
"    \"git_tags_url\": \"https://api.github.com/repos/evanphx/ace/git/tags{/sha}\",\n" +
"    \"git_refs_url\": \"https://api.github.com/repos/evanphx/ace/git/refs{/sha}\",\n" +
"    \"trees_url\": \"https://api.github.com/repos/evanphx/ace/git/trees{/sha}\",\n" +
"    \"statuses_url\": \"https://api.github.com/repos/evanphx/ace/statuses/{sha}\",\n" +
"    \"languages_url\": \"https://api.github.com/repos/evanphx/ace/languages\",\n" +
"    \"stargazers_url\": \"https://api.github.com/repos/evanphx/ace/stargazers\",\n" +
"    \"contributors_url\": \"https://api.github.com/repos/evanphx/ace/contributors\",\n" +
"    \"subscribers_url\": \"https://api.github.com/repos/evanphx/ace/subscribers\",\n" +
"    \"subscription_url\": \"https://api.github.com/repos/evanphx/ace/subscription\",\n" +
"    \"commits_url\": \"https://api.github.com/repos/evanphx/ace/commits{/sha}\",\n" +
"    \"git_commits_url\": \"https://api.github.com/repos/evanphx/ace/git/commits{/sha}\",\n" +
"    \"comments_url\": \"https://api.github.com/repos/evanphx/ace/comments{/number}\",\n" +
"    \"issue_comment_url\": \"https://api.github.com/repos/evanphx/ace/issues/comments/{number}\",\n" +
"    \"contents_url\": \"https://api.github.com/repos/evanphx/ace/contents/{+path}\",\n" +
"    \"compare_url\": \"https://api.github.com/repos/evanphx/ace/compare/{base}...{head}\",\n" +
"    \"merges_url\": \"https://api.github.com/repos/evanphx/ace/merges\",\n" +
"    \"archive_url\": \"https://api.github.com/repos/evanphx/ace/{archive_format}{/ref}\",\n" +
"    \"downloads_url\": \"https://api.github.com/repos/evanphx/ace/downloads\",\n" +
"    \"issues_url\": \"https://api.github.com/repos/evanphx/ace/issues{/number}\",\n" +
"    \"pulls_url\": \"https://api.github.com/repos/evanphx/ace/pulls{/number}\",\n" +
"    \"milestones_url\": \"https://api.github.com/repos/evanphx/ace/milestones{/number}\",\n" +
"    \"notifications_url\": \"https://api.github.com/repos/evanphx/ace/notifications{?since,all,participating}\",\n" +
"    \"labels_url\": \"https://api.github.com/repos/evanphx/ace/labels{/name}\",\n" +
"    \"releases_url\": \"https://api.github.com/repos/evanphx/ace/releases{/id}\",\n" +
"    \"created_at\": \"2013-07-12T18:12:26Z\",\n" +
"    \"updated_at\": \"2013-07-12T18:12:26Z\",\n" +
"    \"pushed_at\": \"2013-07-12T07:04:43Z\",\n" +
"    \"git_url\": \"git://github.com/evanphx/ace.git\",\n" +
"    \"ssh_url\": \"git@github.com:evanphx/ace.git\",\n" +
"    \"clone_url\": \"https://github.com/evanphx/ace.git\",\n" +
"    \"svn_url\": \"https://github.com/evanphx/ace\",\n" +
"    \"homepage\": \"http://ace.ajax.org\",\n" +
"    \"size\": 20170,\n" +
"    \"stargazers_count\": 0,\n" +
"    \"watchers_count\": 0,\n" +
"    \"language\": \"JavaScript\",\n" +
"    \"has_issues\": false,\n" +
"    \"has_downloads\": true,\n" +
"    \"has_wiki\": true,\n" +
"    \"forks_count\": 0,\n" +
"    \"mirror_url\": null,\n" +
"    \"open_issues_count\": 0,\n" +
"    \"forks\": 0,\n" +
"    \"open_issues\": 0,\n" +
"    \"watchers\": 0,\n" +
"    \"default_branch\": \"master\"\n" +
"  },\n" +
"  {\n" +
"    \"id\": 3814699,\n" +
"    \"name\": \"benchmark-ips\",\n" +
"    \"full_name\": \"evanphx/benchmark-ips\",\n" +
"    \"owner\": {\n" +
"      \"login\": \"evanphx\",\n" +
"      \"id\": 7,\n" +
"      \"avatar_url\": \"https://avatars.githubusercontent.com/u/7?\",\n" +
"      \"gravatar_id\": \"540cb3b3712ffe045113cb03bab616a2\",\n" +
"      \"url\": \"https://api.github.com/users/evanphx\",\n" +
"      \"html_url\": \"https://github.com/evanphx\",\n" +
"      \"followers_url\": \"https://api.github.com/users/evanphx/followers\",\n" +
"      \"following_url\": \"https://api.github.com/users/evanphx/following{/other_user}\",\n" +
"      \"gists_url\": \"https://api.github.com/users/evanphx/gists{/gist_id}\",\n" +
"      \"starred_url\": \"https://api.github.com/users/evanphx/starred{/owner}{/repo}\",\n" +
"      \"subscriptions_url\": \"https://api.github.com/users/evanphx/subscriptions\",\n" +
"      \"organizations_url\": \"https://api.github.com/users/evanphx/orgs\",\n" +
"      \"repos_url\": \"https://api.github.com/users/evanphx/repos\",\n" +
"      \"events_url\": \"https://api.github.com/users/evanphx/events{/privacy}\",\n" +
"      \"received_events_url\": \"https://api.github.com/users/evanphx/received_events\",\n" +
"      \"type\": \"User\",\n" +
"      \"site_admin\": false\n" +
"    },\n" +
"    \"private\": false,\n" +
"    \"html_url\": \"https://github.com/evanphx/benchmark-ips\",\n" +
"    \"description\": \"Provides iteration per second benchmarking for Ruby\",\n" +
"    \"fork\": false,\n" +
"    \"url\": \"https://api.github.com/repos/evanphx/benchmark-ips\",\n" +
"    \"forks_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/forks\",\n" +
"    \"keys_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/keys{/key_id}\",\n" +
"    \"collaborators_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/collaborators{/collaborator}\",\n" +
"    \"teams_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/teams\",\n" +
"    \"hooks_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/hooks\",\n" +
"    \"issue_events_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/issues/events{/number}\",\n" +
"    \"events_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/events\",\n" +
"    \"assignees_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/assignees{/user}\",\n" +
"    \"branches_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/branches{/branch}\",\n" +
"    \"tags_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/tags\",\n" +
"    \"blobs_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/git/blobs{/sha}\",\n" +
"    \"git_tags_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/git/tags{/sha}\",\n" +
"    \"git_refs_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/git/refs{/sha}\",\n" +
"    \"trees_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/git/trees{/sha}\",\n" +
"    \"statuses_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/statuses/{sha}\",\n" +
"    \"languages_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/languages\",\n" +
"    \"stargazers_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/stargazers\",\n" +
"    \"contributors_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/contributors\",\n" +
"    \"subscribers_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/subscribers\",\n" +
"    \"subscription_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/subscription\",\n" +
"    \"commits_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/commits{/sha}\",\n" +
"    \"git_commits_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/git/commits{/sha}\",\n" +
"    \"comments_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/comments{/number}\",\n" +
"    \"issue_comment_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/issues/comments/{number}\",\n" +
"    \"contents_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/contents/{+path}\",\n" +
"    \"compare_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/compare/{base}...{head}\",\n" +
"    \"merges_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/merges\",\n" +
"    \"archive_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/{archive_format}{/ref}\",\n" +
"    \"downloads_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/downloads\",\n" +
"    \"issues_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/issues{/number}\",\n" +
"    \"pulls_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/pulls{/number}\",\n" +
"    \"milestones_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/milestones{/number}\",\n" +
"    \"notifications_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/notifications{?since,all,participating}\",\n" +
"    \"labels_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/labels{/name}\",\n" +
"    \"releases_url\": \"https://api.github.com/repos/evanphx/benchmark-ips/releases{/id}\",\n" +
"    \"created_at\": \"2012-03-24T02:58:55Z\",\n" +
"    \"updated_at\": \"2014-06-27T15:11:43Z\",\n" +
"    \"pushed_at\": \"2014-06-18T22:39:01Z\",\n" +
"    \"git_url\": \"git://github.com/evanphx/benchmark-ips.git\",\n" +
"    \"ssh_url\": \"git@github.com:evanphx/benchmark-ips.git\",\n" +
"    \"clone_url\": \"https://github.com/evanphx/benchmark-ips.git\",\n" +
"    \"svn_url\": \"https://github.com/evanphx/benchmark-ips\",\n" +
"    \"homepage\": \"\",\n" +
"    \"size\": 410,\n" +
"    \"stargazers_count\": 69,\n" +
"    \"watchers_count\": 69,\n" +
"    \"language\": \"Ruby\",\n" +
"    \"has_issues\": true,\n" +
"    \"has_downloads\": true,\n" +
"    \"has_wiki\": true,\n" +
"    \"forks_count\": 12,\n" +
"    \"mirror_url\": null,\n" +
"    \"open_issues_count\": 2,\n" +
"    \"forks\": 12,\n" +
"    \"open_issues\": 2,\n" +
"    \"watchers\": 69,\n" +
"    \"default_branch\": \"master\"\n" +
"  },\n" +
"  {\n" +
"    \"id\": 1574968,\n" +
"    \"name\": \"benchmark_suite\",\n" +
"    \"full_name\": \"evanphx/benchmark_suite\",\n" +
"    \"owner\": {\n" +
"      \"login\": \"evanphx\",\n" +
"      \"id\": 7,\n" +
"      \"avatar_url\": \"https://avatars.githubusercontent.com/u/7?\",\n" +
"      \"gravatar_id\": \"540cb3b3712ffe045113cb03bab616a2\",\n" +
"      \"url\": \"https://api.github.com/users/evanphx\",\n" +
"      \"html_url\": \"https://github.com/evanphx\",\n" +
"      \"followers_url\": \"https://api.github.com/users/evanphx/followers\",\n" +
"      \"following_url\": \"https://api.github.com/users/evanphx/following{/other_user}\",\n" +
"      \"gists_url\": \"https://api.github.com/users/evanphx/gists{/gist_id}\",\n" +
"      \"starred_url\": \"https://api.github.com/users/evanphx/starred{/owner}{/repo}\",\n" +
"      \"subscriptions_url\": \"https://api.github.com/users/evanphx/subscriptions\",\n" +
"      \"organizations_url\": \"https://api.github.com/users/evanphx/orgs\",\n" +
"      \"repos_url\": \"https://api.github.com/users/evanphx/repos\",\n" +
"      \"events_url\": \"https://api.github.com/users/evanphx/events{/privacy}\",\n" +
"      \"received_events_url\": \"https://api.github.com/users/evanphx/received_events\",\n" +
"      \"type\": \"User\",\n" +
"      \"site_admin\": false\n" +
"    },\n" +
"    \"private\": false,\n" +
"    \"html_url\": \"https://github.com/evanphx/benchmark_suite\",\n" +
"    \"description\": \"A set of enhancements to benchmark.rb\",\n" +
"    \"fork\": false,\n" +
"    \"url\": \"https://api.github.com/repos/evanphx/benchmark_suite\",\n" +
"    \"forks_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/forks\",\n" +
"    \"keys_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/keys{/key_id}\",\n" +
"    \"collaborators_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/collaborators{/collaborator}\",\n" +
"    \"teams_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/teams\",\n" +
"    \"hooks_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/hooks\",\n" +
"    \"issue_events_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/issues/events{/number}\",\n" +
"    \"events_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/events\",\n" +
"    \"assignees_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/assignees{/user}\",\n" +
"    \"branches_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/branches{/branch}\",\n" +
"    \"tags_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/tags\",\n" +
"    \"blobs_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/git/blobs{/sha}\",\n" +
"    \"git_tags_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/git/tags{/sha}\",\n" +
"    \"git_refs_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/git/refs{/sha}\",\n" +
"    \"trees_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/git/trees{/sha}\",\n" +
"    \"statuses_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/statuses/{sha}\",\n" +
"    \"languages_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/languages\",\n" +
"    \"stargazers_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/stargazers\",\n" +
"    \"contributors_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/contributors\",\n" +
"    \"subscribers_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/subscribers\",\n" +
"    \"subscription_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/subscription\",\n" +
"    \"commits_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/commits{/sha}\",\n" +
"    \"git_commits_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/git/commits{/sha}\",\n" +
"    \"comments_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/comments{/number}\",\n" +
"    \"issue_comment_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/issues/comments/{number}\",\n" +
"    \"contents_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/contents/{+path}\",\n" +
"    \"compare_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/compare/{base}...{head}\",\n" +
"    \"merges_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/merges\",\n" +
"    \"archive_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/{archive_format}{/ref}\",\n" +
"    \"downloads_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/downloads\",\n" +
"    \"issues_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/issues{/number}\",\n" +
"    \"pulls_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/pulls{/number}\",\n" +
"    \"milestones_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/milestones{/number}\",\n" +
"    \"notifications_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/notifications{?since,all,participating}\",\n" +
"    \"labels_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/labels{/name}\",\n" +
"    \"releases_url\": \"https://api.github.com/repos/evanphx/benchmark_suite/releases{/id}\",\n" +
"    \"created_at\": \"2011-04-06T01:02:57Z\",\n" +
"    \"updated_at\": \"2014-06-18T20:36:55Z\",\n" +
"    \"pushed_at\": \"2012-05-11T15:47:41Z\",\n" +
"    \"git_url\": \"git://github.com/evanphx/benchmark_suite.git\",\n" +
"    \"ssh_url\": \"git@github.com:evanphx/benchmark_suite.git\",\n" +
"    \"clone_url\": \"https://github.com/evanphx/benchmark_suite.git\",\n" +
"    \"svn_url\": \"https://github.com/evanphx/benchmark_suite\",\n" +
"    \"homepage\": \"\",\n" +
"    \"size\": 128,\n" +
"    \"stargazers_count\": 45,\n" +
"    \"watchers_count\": 45,\n" +
"    \"language\": \"Ruby\",\n" +
"    \"has_issues\": true,\n" +
"    \"has_downloads\": true,\n" +
"    \"has_wiki\": true,\n" +
"    \"forks_count\": 6,\n" +
"    \"mirror_url\": null,\n" +
"    \"open_issues_count\": 1,\n" +
"    \"forks\": 6,\n" +
"    \"open_issues\": 1,\n" +
"    \"watchers\": 45,\n" +
"    \"default_branch\": \"master\"\n" +
"  },";
    
}
