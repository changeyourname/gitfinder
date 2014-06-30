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

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import utils.files;

/**
 *
 * @author Nuno Brito, 29th of July 2014 in Darsmtadt, Germany
 */
public class rep {
    
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
       
        } catch (Exception ex) {
            Logger.getLogger(rep.class.getName()).log(Level.SEVERE, null, ex);
        }
 
             System.out.println("--> " + location);
    }

}
