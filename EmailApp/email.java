package EmailApp;

import java.util.Scanner;

public class email {
	
	private String firstName;
	private String lastName;
	private String password;
	private String department;
	private String email;
	private int mailboxCapacity = 500;
	private int defaultPasswordLength = 10;
	private String alternateEmail;
	private String companySuffix = "whatevercompany.com";
	
	// Create constructor to receive first name and last name.
	
	public email(String firstName, String lastName) {
		
		this.firstName = firstName;
		this.lastName = lastName;
		//System.out.println("Email created: " + this.firstName + " " + this.lastName);
		
		// Call a method asking for the department - return the department
		
		this.department = setDepartment();
		//System.out.println("Department: " + this.department);
		
		// Call a method that returns a random password
		
		this.password = randomPass(defaultPasswordLength);
		System.out.println("Your password is: " + this.password);
		
		// Combine elements to generate email
		
		email = firstName.toLowerCase() + "." + lastName.toLowerCase() + "@" + department + "." + companySuffix;
		//System.out.println("Your email is: " + email);
		
		
	}
	
	// Ask for department
	
	private String setDepartment() {
		
		System.out.print("New Worker: " + firstName + " " + lastName + ". Department Codes:\n1 for Sales\n2 for Development\n3 for Accounting\n4 for None\nEnter department: ");
		
		Scanner in = new Scanner(System.in);
		int deptChoice = in.nextInt();
		if(deptChoice == 1)
		{
			return "Sales";
		}
		else if(deptChoice == 2)
		{
			return "Development";
		}
		else if(deptChoice == 3)
		{
			return "Accounting";
		}
		else
		{
			return "";
		}
	}
	
	// Generate a random password
	
	private String randomPass(int length) {
		
		String passwordSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789!!£$%^&*()@:,.<>/#"; // can add more if needed later...
		char[] password = new char[length];
		for(int i = 0; i < length; i++)
		{
			int rand = (int) (Math.random() * passwordSet.length());
			password[i] = passwordSet.charAt(rand);
		}
		return new String(password); 
		
	}
	
	// Set the mailbox capacity
	
	public void setMailboxCapacity(int capacity) {
		this.mailboxCapacity = capacity;
	}
	
	// Set the alternate email
	
	public void setAlternateEmail(String altEmail) {
		this.alternateEmail = altEmail;
	}
	
	// Change the password
	
	public void changePassword(String password) {
		this.password = password;
	}
	
	public int getMailboxCapacity() { return mailboxCapacity; }
	public String getAlternatetEmail() { return alternateEmail; }
	public String getPassword() { return password; }
	
	public String showInfo() {
		return "Display Name: " + firstName + " " + lastName +
				"\nCompany Email: " + email + "\nMailbox Capacity: " +
				mailboxCapacity + "mb";
	}
	
}
