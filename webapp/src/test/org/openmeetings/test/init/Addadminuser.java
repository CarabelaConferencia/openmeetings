package org.openmeetings.test.init;

import junit.framework.TestCase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.openmeetings.app.data.user.Usermanagement;
import org.openmeetings.app.data.user.Statemanagement;
import org.openmeetings.test.adresses.TestAddEmailToAdress;

public class Addadminuser extends TestCase{
	
	private static final Logger log = LoggerFactory.getLogger(Addadminuser.class);
	
	public Addadminuser(String testname){
		super(testname);
	}
	
	public void testAddADminUser() throws Exception{
		Statemanagement.getInstance().addState("Deutschland");
		
		//(long user_level,String login, String Userpass, String lastname, String firstname, String email, int age, String street, String additionalname, String fax, String zip, long states_id, String town, long language_id)
		long user_id = Usermanagement.getInstance().registerUserInit(new Long(3), 3, 1, 1, "swagner4", "67810", "Wagner", "Sebastian", "seba.wagner@gmail.com", new java.util.Date(), "Bleichstraže", "92", "fax number", "75173", 1, "Pforzheim", 1, true, null);
		
		log.error("new User: "+user_id);
	}

}
