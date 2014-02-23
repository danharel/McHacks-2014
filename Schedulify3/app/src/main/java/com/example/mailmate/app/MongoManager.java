import com.mongodb.*;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;

/*
 * Wrapper class for MongoDB
 * Written by Dan Harel and Edmund Qiu
 */
public class MongoManager {
    public static final String BRIAN_SITE = "cloud.brianyang.me";
    public static final int MONGO_SOCKET = 27017;

    private MongoClient mongoClient;
    private DB db;
    private DBCollection contacts;

    private TwilioWrapper twilio;

    private HashSet<String> groups;

    /*
     * Creates new MongoManager object
     */
    public MongoManager() {

        //MongoDB setup
        try {
            //Connect to Brian's server
            mongoClient = new MongoClient(BRIAN_SITE, MONGO_SOCKET);
        } catch (Exception e) {
            Thread.currentThread().getStackTrace();
        }
        db = mongoClient.getDB("mongoDB");
        contacts = db.getCollection("contacts");

        //Set of group names, also important for Mongo
        groups = new HashSet<String>();

        //Twilio setup
        twilio = new TwilioWrapper();

    }

    /**
     *
     * @return
     */
    public ArrayList<Contact> getAllContacts()
        {
            DBCursor cursor = contacts.find();
            ArrayList<Contact> wrappedContacts = new ArrayList<Contact>();

            while (cursor.hasNext()) {
                BasicDBObject curr = (BasicDBObject)cursor.next();
                Contact c = DBObjectToContact(curr);
                wrappedContacts.add(c);
            }

            return wrappedContacts;
    }

    /**
     *
     * @param contactName
     * @return
     */
    public BasicDBObject getContactByName(String contactName)
    {
        BasicDBObject query = new BasicDBObject("name", contactName);
        DBCursor cursor = contacts.find(query);
        if (cursor.hasNext())
            return (BasicDBObject)cursor.next();
        else
            return null;
    }

    /**
     *
     * @param contact
     * @return
     */
    public Contact DBObjectToContact(BasicDBObject contact)
    {
        Contact c = new Contact();
        c.setName((String)contact.get("name"));
        c.setEmails((ArrayList<String>)contact.get("emails"));
        c.setNumbers((ArrayList<String>)contact.get("numbers"));
        c.setFacebooks((ArrayList<String>)contact.get("facebook"));
        c.setGroups((ArrayList<String>)contact.get("groups"));
        return c;
    }

    /**
     * Just note that if you write "email" instead of "emails" or "group" instead
     * of "groups", then it won't work because of the way MongoDB works.
     *
     * @param contactName
     * @param updatedField "name", "emails", "facebook", "groups", or "numbers"
     * @param newValue
     * @return true if succeeded, false if failed.
     */
    public boolean updateContact(String contactName, String updatedField,
                                 Object newValue)
    {
        if (contactName == null) return false;

        //Just in case the user typed it wrong, let's tell them
        switch(updatedField)
        {
        	case "groups":
        		groups.add((String)newValue);
            case "name":
            case "emails":
            case "facebook":
            case "numbers":
                BasicDBObject change = new BasicDBObject("$set",
                        new BasicDBObject().append(updatedField, newValue));
                BasicDBObject query = this.getContactByName(contactName);
                contacts.update(query, change);

            return true;
        }

        System.out.println("Use the right 'updatedField' name!");
        return false;

    }

    public boolean addContact(String name) {
    	return addContact(name, new ArrayList<String>(), 
    			new ArrayList<String>(), new ArrayList<String>(), 
    			new ArrayList<String>());
    }
    
	/**
	 * Adds a new contact to the database
	 * 
	 * @param	name						Name of the contact
	 * @param	emails		ArrayList of emails used by the user
	 * @param	numbers		ArrayList of numbers used by the user
	 * @param	FBusernames	ArrayList of Facebook usernames used by the user
	 * @param	groups		ArrayList of groups the user is in.
	 */
	public boolean addContact(String name, ArrayList<String> emails, 
			ArrayList<String> numbers, ArrayList<String> FBusernames, 
			ArrayList<String> groups) {
		
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

            //Add the groups this Contact belongs to to the set
            for (String newGroup : groups)
            {
                this.groups.add(newGroup);
            }

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
            //Remove the object and save what group it is in
			BasicDBObject curr = new BasicDBObject("name", name);
			curr = (BasicDBObject)contacts.findOne(curr);
            ArrayList<String> objGroups = (ArrayList<String>)curr.get("groups");
			contacts.remove(new BasicDBObject("name", name));

            //Test to see if anything on MongoDB is associated with each
            //group is the ArrayList now that it's gone:
            for (String group : objGroups)
            {
                if (listContactsInGroup(group).size() == 0)
                {
                    groups.remove(group);
                }
            }

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
	 * @param user		Name of user to message.
	 * @param subject	Subject of message.
	 * @param message	Message to send.
	 * @param method	Specified method of communication
	 */
	public void messageUser(BasicDBObject user, String subject,
                            String message, String method) {
		
		BasicDBObject curr = user;
		
		//Send a message through all methods of communication specified in "methods" 
		if (method.equals("phone")) {
			//Send to all numbers
			for (int j = 0; j < ((ArrayList<String>) curr.get("numbers")).size(); j++) {
				System.out.println("Sending a txt to " +
                        ((ArrayList<String>) curr.get("numbers")).get(j));
				//twilio.addMessage( ((ArrayList<String>) curr.get("numbers")).
				// get(j), message);
			}
			//twilio.sendBatch();
		}
		else if (method.equals("email")) {
			//Send to all emails
			for (int j = 0; j < ((ArrayList<String>) curr.get("emails")).size(); j++) {
				System.out.println("Sending an email to " +
                        ((ArrayList<String>) curr.get("emails")).get(j));
				//SendMail.send( ((ArrayList<String>) curr.get("emails")).
				// get(j), subject, message);
			}
		}
		else if (method.equals("facebook")) {
			//Send to all Facebook accounts
			for (int j = 0;
                 j < ((ArrayList<String>) curr.get("facebook")).size(); j++) {
				System.out.println("Sending a private message to " +
                        ((ArrayList<String>) curr.get("facebook")).get(j));
				//facebook.PM( ((ArrayList<String>) curr.get("FBusernames")).
				// get(j), message);
			}
		}
		else {
			System.out.println("Invalid method");
		}
	}
	
	/**
	 * Sends a message to specific user via all methods of communication
	 * 
	 * @param user		Name of the user to message
	 * @param subject	Subject of the message
	 * @param message	Message to send
	 */
	public void messageUser(BasicDBObject user, String subject, String message) {
		
		messageUser(user, subject, message, "phone");
		messageUser(user, subject, message, "email");
		messageUser(user, subject, message, "facebook");
		
	}

    /**
     * Prints out a list of every group
     */
	public void printGroups() {
		for (String group : getGroupList())
			System.out.println(group);
	}

    /**
     * Returns a list of every group
     * @return a list of every group
     */
    public ArrayList<String> getGroupList()
    {
        ArrayList<String> allGroups = new ArrayList<>();
        for (String group : groups)
        {
            allGroups.add(group);
        }
        return allGroups;
    }

    public ArrayList<BasicDBObject> listContactsInGroup(String groupName)
    {
        ArrayList<BasicDBObject> contactList = new ArrayList<>();

        BasicDBObject query = new BasicDBObject("groups", groupName);
        DBCursor cursor = contacts.find(query);
        while (cursor.hasNext()){
            BasicDBObject curr = (BasicDBObject)cursor.next();
            contactList.add(curr);
        }
        return contactList;
    }

    public ArrayList<Contact> listContactObjInGroup(String groupName)
    {
        ArrayList<Contact> contactList = new ArrayList<>();
        for (BasicDBObject dbObject : listContactsInGroup(groupName))
        {
            contactList.add(DBObjectToContact(dbObject));
        }
        return contactList;
    }

    public ArrayList<String> listContactNamesInGroup(String groupName)
    {
        ArrayList<String> contactList = new ArrayList<>();
        for (Contact contactObj : listContactObjInGroup(groupName))
        {
            contactList.add(contactObj.getName());
        }
        return contactList;
    }
	
	/**
	 * Sends a message to a specified group
	 * 
	 * @param	group					The group to send the message to
	 * @param	message					The message to send
	 * @param	methods		            The methods of communication to use
	 * 												-"phone"
	 * 												-"email"
	 * 												-"facebook"
	 */
	public void sendGroup(String group, String subject,
                          String message, ArrayList<String> methods) {
        ArrayList<BasicDBObject> contactsInGroup = listContactsInGroup(group);
		for (BasicDBObject currentContact : contactsInGroup)
        {
			System.out.println(currentContact);
			messageUser(currentContact, subject, message);
		}
	}

    //Entry point for tests
    public static void main(String[] args) {

        MongoManager test = new MongoManager();
        test.contacts.drop();

        // Original Dan
        ArrayList<String> groups = new ArrayList<String>();
        groups.add("Yes");
        groups.add("No");
        test.addContact("Dan", new ArrayList<String>(),
                new ArrayList<String>(), new ArrayList<String>(), groups );

        //Tell us about Dan
        System.out.println(test.getContactByName("Dan").toString());

        //Update Dan
        ArrayList<String> newNumbers = new ArrayList<String>();
        newNumbers.add("900190019001");
        newNumbers.add("123");
        newNumbers.add("1234125");
        test.updateContact("Dan", "numbers", newNumbers);

        //Tell us about Dan
        System.out.println(test.getContactByName("Dan").toString());

        //No more Dan.
        System.out.println(test.removeContact("Dan"));

        //Test now...
        System.out.println(test.groups.size());

        test.mongoClient.dropDatabase("mongoDB");

    }

}
