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

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import org.apache.commons.lang3.StringEscapeUtils;


/**
 *
 * @author Nuno Brito, 30th of June 2014 in Darmstadt, Germany
 */
public class test {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws UnsupportedEncodingException {
        String test = "ncdc/logstash-openshift-plugin/Ruby/Plugin for LogStash to retrieve OpenShift gear environment variables and add them as fields to the events";

        String send = URLEncoder.encode(test, "UTF-8" );
        String receive = URLDecoder.decode(send, "UTF-8");

        System.out.println(send);
        System.out.println(receive);
        
        
    }    
}
