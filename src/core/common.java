/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-06-30T14:04:22Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: common.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Where we keep global common of the program.</text> 
 */

package core;

import java.io.File;
import java.util.prefs.Preferences;

/**
 *
 * @author Nuno Brito, 30th of June 2014 in Darsmtadt, Germany
 */
public class common {
    static String username = "";
    static String password = "";
    static Preferences prefs;
    final static String filenameUsers = "users.txt";
    final static String filenameRepositories = "repositories.txt";
    
    // check if our file already exists
    final static File fileUsers = new File(common.filenameUsers);
    final static File fileRepositories = new File(common.filenameRepositories);

}
