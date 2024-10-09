package EmailApp;

public class emailApp {

	public static void main(String[] args) {
		
		/* Scenario/ objective of program:
		 
		 * I am an IT Support Administrator Specialist and I  
		 * am charged with the task of creating accounts for new hires
		 
		 It should do the following:
		 - Generate an email with the following syntax: first name, last name, @department, company.com
		 - Determine the department: (Sales, development, accounting). If none leave blank.
		 - Generate a random string for a password
		 - Have set methods to change the password, set the mailbox capacity,
		   and define an alternate email address.
		 - Have get methods to display the name, email,m and mailbox capacity. 
		   	
		*/
		
		email email1 = new email("Gavin", "Sidhu"); 
		
		System.out.println(email1.showInfo());
		
	}

}
