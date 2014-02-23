import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.WriteConcern;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBCursor;
import com.mongodb.ServerAddress;

import java.util.Arrays;
import java.util.ArrayList;

/**
 * Wrapper class for MongoDB
 */
public class MongoManager {

	MongoClient mongoClient;
	DB db;
	DBCollection contacts;

	TwilioWrapper twilio;

	/**
	 * Creates new MongoManager object
	 */
	public MongoManager() {
		try {
			mongoClient = new MongoClient("localhost");
		} catch (Exception e) {
			Thread.currentThread().getStackTrace();
		}
		db = mongoClient.getDB("mongoDB");
		contacts = db.getCollection("contacts");

		twilio = new TwilioWrapper();
	}

	/**
	 * Adds a new contact to the database
	 * 
	 * @param	String name						Name of the contact
	 * @param	ArrayList<String> emails		ArrayList of emails used by the user
	 * @param	ArrayList<String> numbers		ArrayList of numbers used by the user
	 * @param	ArrayList<String> FBusernames	ArrayList of Facebook usernames used by the user
	 * @param	ArrayList<String> groups		ArrayList of groups the user is in.
	 */
	public boolean addContact(String name, ArrayList<String> emails, ArrayList<String> numbers,
			ArrayList<String> FBusernames, ArrayList<String> groups) {
		
		try{
			contacts.find( new BasicDBObject("name", name)).next();
		}
		catch (Exception e) {
		
			BasicDBObject newContact = new BasicDBObject("name", name);
			newContact.append("emails", emails);
			newContact.append("numbers", numbers);
			newContact.append("facebook", FBusernames);
			newContact.append("groups", groups);

			contacts.insert(newContact);
			
			return true;
		}
		return false;
	}

	/**
	 * Removes a user from the database
	 * 
	 * @param name	The name of the user to be removed
	 * @return		The user that was removed
	 */
	public BasicDBObject removeContact(String name) {
		try {
			BasicDBObject curr = new BasicDBObject("name", name);
			curr = (BasicDBObject) contacts.findOne(curr);
		
			contacts.remove( new BasicDBObject("name", name));
		
			return curr;
		}
		catch (Exception e) {
			System.out.println("User does not exist");
			return null;
		}
		
	}
	
	/**
	 * Prints out all contacts saved
	 */
	public void printContacts() {

		DBCursor cursor = contacts.find();

		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
	}
	
	/**
	 * Sends message to specified user via specified form of communication. 
	 * 
	 * @param name		Name of user to message.
	 * @param subject	Subject of message.
	 * @param message	Message to send.
	 * @param method	Specified method of communication
	 */
	public void messageUser(BasicDBObject user, String subject, String message, String method) {
		
		BasicDBObject curr = user;
		
		//Send a message through all methods of communication specified in "methods" 
		if (method.equals("phone")) {
			//Send to all numbers
			for (int j = 0; j < ((ArrayList<String>) curr.get("numbers")).size(); j++) {
				System.out.println("Sending a txt to " + ((ArrayList<String>) curr.get("numbers")).get(j));
				//twilio.addMessage( ((ArrayList<String>) curr.get("numbers")).get(j), message);
			}
			//twilio.sendBatch();
		}
		else if (method.equals("email")) {
			//Send to all emails
			for (int j = 0; j < ((ArrayList<String>) curr.get("emails")).size(); j++) {
				System.out.println("Sending an email to " + ((ArrayList<String>) curr.get("emails")).get(j));
				//SendMail.send( ((ArrayList<String>) curr.get("emails")).get(j), subject, message);
			}
		}
		else if (method.equals("facebook")) {
			//Send to all Facebook accounts
			for (int j = 0; j < ((ArrayList<String>) curr.get("facebook")).size(); j++) {
				System.out.println("Sending a private message to " + ((ArrayList<String>) curr.get("facebook")).get(j));
				//facebook.PM( ((ArrayList<String>) curr.get("FBusernames")).get(j), message);
			}
		}
		else {
			System.out.println("Invalid method");
		}
	}
	
	/**
	 * Sends a message to specific user via all methods of communication
	 * 
	 * @param name		Name of the user to message 
	 * @param subject	Subject of the message
	 * @param message	Message to send
	 */
	public void messageUser(BasicDBObject user, String subject, String message) {
		
		messageUser(user, subject, message, "phone");
		messageUser(user, subject, message, "email");
		messageUser(user, subject, message, "facebook");
		
	}
	
	/**
	 * Sends a message to a specified group
	 * 
	 * @param	String group					The group to send the message to
	 * @param	String message					The message to send
	 * @param	ArrayList<String> methods		The methods of communication to use
	 * 												-"phone"
	 * 												-"email"
	 * 												-"facebook"
	 */
	public void sendGroup(String group, String subject, String message, ArrayList<String> methods) {
		
		BasicDBObject query = new BasicDBObject("groups", group );
		DBCursor cursor = contacts.find(query);
		
		while (cursor.hasNext()){
			BasicDBObject curr = (BasicDBObject)cursor.next();
			System.out.println(curr);
			messageUser(curr, subject, message);
		}
			
	}

	public static void main(String[] args) {

		MongoManager test = new MongoManager();
		test.contacts.drop();

		System.out.println("Hello");

		//String[] lol = new String[10];
		//String[] groups = { "Yes", "No" };
		
		ArrayList<String> groups = new ArrayList<String>();
		groups.add("Yes");
		groups.add("No");

		//Test whether or not you can add the same person twice.
		//Should not be able to.
		System.out.println(test.addContact("Dan", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), groups ));
		System.out.println(test.addContact("Dan", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), groups ));
		//Success.
		
		ArrayList<String> methods = new ArrayList<String>();
		methods.add("phone");
		methods.add("facebook");
		test.sendGroup("Yes", "Hello", "Hello", methods);
		
		//Test removeContact()
		test.removeContact("Dan");		
		System.out.println("Users: ");
		test.printContacts();
		//Success

		System.out.println("Hello");

	}

}
