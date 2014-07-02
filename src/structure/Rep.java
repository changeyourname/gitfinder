/*
 * SPDXVersion: SPDX-1.1
 * Creator: Person: Nuno Brito (nuno.brito@triplecheck.de)
 * Creator: Organization: TripleCheck (contact@triplecheck.de)
 * Created: 2014-07-01T19:17:10Z
 * LicenseName: EUPL-1.1-without-appendix
 * FileName: Rep.java  
 * FileType: SOURCE
 * FileCopyrightText: <text> Copyright 2014 Nuno Brito, TripleCheck </text>
 * FileComment: <text> Defines the structure of a repository</text> 
 */


package structure;

/**
 *
 * @author Nuno Brito, 1st of July 2014 in Darmstadt, Germany
 */
public class Rep {
    private String 
            idUser = "",
            idRepository = "",
            language = "",
            description = "";

    /**
     * Creates a single line ready to be written on repositories.txt
     * @return 
     */
    public String getOneline(){
        // create the basic line
        String result = idUser + "/" + idRepository + " " + language;
        // do we have a description?
        if(description.isEmpty() == false){
            result = result.concat(" " + description);
        }
        // all done
        return result;
    }
    
    /**
     * Provides a line ready for submitting as data onto a server
     * @return A line of text that represents a URL
     */
    public String getWebSubmit(){
        String tempDescription =  description;
        // we still need to declare no description
        if(description.isEmpty()){
            tempDescription = "none";
        }
        // create our URL
        final String result = 
                        idUser 
                + "/" + idRepository
                + "/" + utils.text.htmlEncode(language)
                + "/" + utils.text.htmlEncode(tempDescription);
        
        return result;
    }
    
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
    }

    public String getIdRepository() {
        return idRepository;
    }

    public void setIdRepository(String idRepository) {
        this.idRepository = idRepository;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        // we don't want spaces in language descriptions
        this.language = language.replace(" ", "_");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUID() {
        return idUser + "/" + idRepository;
    }
            
            
            
}
