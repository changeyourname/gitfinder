/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-07-02T18:30:20Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: User.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Defines the structure of a user</text> 
 */


package structure;

/**
 *
 * @author Nuno Brito, 2nd of July 2014 in Darmstadt, Germany
 */
public class User {
    private final String idUser;
    // time line
    private final long timeLimit = 1000 * 60
            // comment the line below to change this limit for 1 minute (useful for testing)
            * 60 * 2; // wait 2 hours
            ;
    // when was this object created?
    private final long timeStamp = System.currentTimeMillis();
    
    public boolean isOld(){
        long timeElapsed = System.currentTimeMillis() - timeStamp;
        return timeElapsed > timeLimit;
    }
            
    public User(final String assignedUser){
        // assign a user
        idUser = assignedUser;
    }

    public String getIdUser() {
        return idUser;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
    
}
