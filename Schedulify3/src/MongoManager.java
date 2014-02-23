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
	public void addContact(String name, ArrayList<String> emails, ArrayList<String> numbers,
			ArrayList<String> FBusernames, ArrayList<String> groups) {
		BasicDBObject newContact = new BasicDBObject("name", name);
		newContact.append("emails", emails);
		newContact.append("numbers", numbers);
		newContact.append("facebook", FBusernames);
		newContact.append("groups", groups);

		contacts.insert(newContact);
	}

	public void printContacts() {
		DBCursor cursor = contacts.find();

		while (cursor.hasNext()) {
			System.out.println(cursor.next());
		}
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
	public void sendGroup(String group, String message, ArrayList<String> methods) {
		
		/*BasicDBObject query = new BasicDBObject(group, new BasicDBObject(new BasicDBObject("$size", new BasicDBObject("$neq", 0) )));
		DBCursor cursor = contacts.find(query);
		
		while (cursor.hasNext())
			System.out.println(cursor.next());*/
		
		BasicDBObject query = new BasicDBObject("groups", group );
		DBCursor cursor = contacts.find(query);
		
		while (cursor.hasNext()){
			BasicDBObject curr = (BasicDBObject)cursor.next();
			System.out.println(curr);
			//Send a message through all methods of communication specified in "methods" 
			for (int i = 0; i < methods.size(); i++) {
				//Send a message to each location specified in the given method's array
				if(methods.get(i).equals("phone")) {
					for (int j = 0; j < ((ArrayList<String>) curr.get("numbers")).size(); j++) {
							System.out.println("Sending a txt to " + ((ArrayList<String>) curr.get("numbers")).get(j));
							//twilio.addMessage( ((ArrayList<String>) curr.get("numbers"))[j], message);
					}
					//twilio.sendBatch();
				}
				else if (methods.get(i).equals( "email")) {
					for (int j = 0; j < ((ArrayList<String>) curr.get("emails")).size(); j++) {
						System.out.println("Sending an email to " + ((ArrayList<String>) curr.get("emails")).get(j));
						//sendgrid.email((ArrayList<String>) curr.get("emails"))[j], message);
					}
				}
				else if (methods.get(i).equals("facebook")) {
					for (int j = 0; j < ((ArrayList<String>) curr.get("facebook")).size(); j++) {
						System.out.println("Sending a private message to " + ((ArrayList<String>) curr.get("facebook")).get(j));
						//facebook.PM((ArrayList<String>) curr.get("FBusernames"))[j], message);
					}
				}
				else {
					System.out.println("Invalid method of communication");
				}
			
			}
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

		//test.addContact("Dan", lol, lol, lol, groups);
		test.addContact("Dan", new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), groups );

		ArrayList<String> methods = new ArrayList<String>();
		methods.add("phone");
		methods.add("facebook");
		test.sendGroup("Yes", "Hello", methods);

		System.out.println("Hello");

	}

}
