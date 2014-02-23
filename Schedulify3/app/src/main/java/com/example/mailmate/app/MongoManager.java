package com.example.mailmate.app;

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

public class MongoManager {

	MongoClient mongoClient;
	DB db;
	DBCollection contacts;

	TwilioWrapper twilio;

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

	public void addContact(String name, String[] emails, String[] numbers,
			String[] FBusernames, String[] groups) {
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

	public void sendGroup(String group, String message, ArrayList<String> methods) {
		
		/*BasicDBObject query = new BasicDBObject(group, new BasicDBObject(new BasicDBObject("$size", new BasicDBObject("$neq", 0) )));
		DBCursor cursor = contacts.find(query);
		
		while (cursor.hasNext())
			System.out.println(cursor.next());*/
		
		BasicDBObject query = new BasicDBObject("groups", group );
		DBCursor cursor = contacts.find(query);
		
		while (cursor.hasNext()){
			BasicDBObject curr = (BasicDBObject)cursor.next();
			//Send a message through all methods of communication specified in "methods" 
			for (int i = 0; i < methods.size(); i++) {
				System.out.println(curr);
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

		String[] lol = new String[10];
		String[] groups = { "Yes", "No" };

		test.addContact("Dan", lol, lol, lol, groups);

		ArrayList<String> methods = new ArrayList<String>();
		methods.add("phone");
		methods.add("facebook");
		test.sendGroup("Yes", "Hello", methods);

		System.out.println("Hello");

	}

}