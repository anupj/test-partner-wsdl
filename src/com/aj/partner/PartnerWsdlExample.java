package com.aj.partner;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Properties;

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.Error;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;


public class PartnerWsdlExample {

	private static final Logger logger = (Logger) LogManager.getLogger(PartnerWsdlExample.class.getName());
	
	private Properties properties;
	
	private String sourceUsername;
	private String sourcePassword;
	private String endpoint;
	
	public static void main(String[] args) {
		
		logger.debug("Entering PartnerWsdlExample.main()");
		PartnerWsdlExample pwe;
		try {
			pwe = new PartnerWsdlExample();

			pwe.testWsdl();
		} catch (IOException ioEx) {
			ioEx.printStackTrace();
			logger.debug("io exception message is: "+ioEx.getMessage());
			logger.debug("io exception cause is: "+ioEx.getCause());
		} catch (ConnectionException connEx) {
			connEx.printStackTrace();
			logger.debug("connection exception message is: "+connEx.getMessage());
			logger.debug("connnection exception cause is: "+connEx.getCause());
		} catch (Exception ex) {
			ex.printStackTrace();
			logger.debug("generic exception message is: "+ex.getMessage());
			logger.debug("generic exception cause is: "+ex.getCause());
		}
		logger.debug("Exiting PartnerWsdlExample.main()");
	}

	public PartnerWsdlExample() throws IOException, ConnectionException {
		this.properties = new Properties();
		this.properties.load(new FileInputStream("config.properties")); 
		this.sourceUsername = this.properties.getProperty("sourceusername");
		this.sourcePassword = this.properties.getProperty("sourcepassword");
		this.endpoint = this.properties.getProperty("endpoint");
	}	
	
	public void testWsdl() throws ConnectionException {
		logger.debug("\n Entering PartnerWsdlExample.testWsdl()");
		PartnerConnection sourceConnection;

		sourceConnection = login(this.sourceUsername, this.sourcePassword);
        logger.debug("test connection: " + sourceConnection.describeGlobal());

        // Set all or none header
        AllOrNoneHeader_element __header  = new AllOrNoneHeader_element();
		__header.setAllOrNone(true);
		sourceConnection.__setAllOrNoneHeader(__header  );

		// SObject
		SObject[] sObjects = new SObject[3];
		
		// Create Account
		SObject parentAccount = new SObject();
		parentAccount.setType("Account"); 
		parentAccount.setField("Name", "TestAccount7");
		parentAccount.setField("AcctExtId__c", "ACCT EXTID 7");
		sObjects[0] = parentAccount;

        // parent account reference
		SObject refAccount = new SObject();
		refAccount.setType("Account");
        refAccount.setField("AcctExtId__c", "ACCT EXTID 7");

		// Contact
		SObject childContact = new SObject();
		childContact.setType("Contact"); 
		childContact.setField("LastName", "TestContact7");
        childContact.setField("ContExtId__c", "CONT EXTID 7");
		childContact.setField("Account", refAccount);
		sObjects[1] = childContact;


		// Opportunity
		SObject childOpp = new SObject();
		childOpp.setType("Opportunity");
        childOpp.setField("Name", "TestOpportunity7");
        Calendar dt = sourceConnection.getServerTimestamp().getTimestamp();
        dt.add(Calendar.DAY_OF_MONTH, 7);
        childOpp.setField("CloseDate", dt);
        childOpp.setField("StageName", "Prospecting");
        childOpp.setField("OppExtId__c", "OPP EXTID 7");
        childOpp.setField("Account", refAccount);
		sObjects[2] = childOpp;

        String result;
		SaveResult[] results = sourceConnection.create(sObjects);
		for (int j = 0; j < results.length; j++) {
          if (results[j].isSuccess()) {
              result = results[j].getId();

              logger.debug(
                      "sobject was created with an ID of: " + result
              );
           } else {
              // There were errors during the create call,
              // go through the errors array and write
              // them to the console
              for (int i = 0; i < results[j].getErrors().length; i++) {
                  Error err = results[j].getErrors()[i];
                  logger.debug("Errors were found on item " + j);
                  logger.debug("Error code: " + err.getStatusCode().toString());
                  logger.debug("Error message: " + err.getMessage());
              }
           }
        }
	}

	private PartnerConnection login(String username, String password) throws ConnectionException {
		ConnectorConfig connector = new ConnectorConfig();
		connector.setUsername(username);
		connector.setPassword(password);

		logger.debug("AuthEndPoint: " + this.endpoint);
		connector.setAuthEndpoint(this.endpoint);
		PartnerConnection connection = new PartnerConnection(connector);

		return connection;
	}

}
