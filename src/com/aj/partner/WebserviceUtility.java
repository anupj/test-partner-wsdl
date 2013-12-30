package com.aj.partner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

/*
 * Utility class for Webservice 
 */
public class WebserviceUtility {
	
	private static final Logger logger = (Logger) LogManager.getLogger(WebserviceUtility.class.getName());
	

	// Converts 15 char salesforce id to 18 char id
	  static String convertto18CharId(String original15charId){
	    
	    if(original15charId == null || original15charId.isEmpty()) return null;
	    original15charId = original15charId.trim();
	    if(original15charId.length() != 15) return null;
	    
	    final String BASE = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456";
	    StringBuilder result = new StringBuilder();
	    
	    try {
	        for(int i = 0; i < 3; i++){
	            StringBuilder tempString = new StringBuilder(original15charId.substring(i*5, i*5+5));
	            tempString.reverse();
	            String binary = "";
	            for(char ch: tempString.toString().toCharArray()) {
	                binary += Character.isUpperCase(ch) ? '1' : '0';
	            }
	            result.append(BASE.charAt(Integer.parseInt(binary,2))); 
	        }
	    } catch(Exception ex) {
	          ex.printStackTrace();
	    }
	    
	    if(result.length() == 0) return null;
	    
	    return original15charId + result.toString();
	    
	  }

}
