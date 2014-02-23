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

/*
 * Wrapper class for MongoDB
 * Written by Dan Harel and Edmund Qiu
 */
public class MongoManager {
    public static final String BRIAN_SITE = "cloud.brianyang.me";
    public static final int MONGO_SOCKET = 27017;

    MongoClient mongoClient;
    DB db;
    DBCollection contacts;

    TwilioWrapper twilio;

    /*
     * Creates new MongoManager object
     */
    public MongoManager() {
        try {
            //mongoClient = new MongoClient("localhost");
            mongoClient = new MongoClient(BRIAN_SITE, MONGO_SOCKET);
        } catch (Exception e) {
            Thread.currentThread().getStackTrace();
        }
        db = mongoClient.getDB("mongoDB");
        contacts = db.getCollection("contacts");

        twilio = new TwilioWrapper();
    }

    /*
     * Adds a new contact to the database
     *
     * @param	String name		Name of the contact
     * @param	ArrayList<String> emails	ArrayList of emails used by the user
     * @param	ArrayList<String> numbers	ArrayList of numbers used by the user
     * @param	ArrayList<String> FBusernames	ArrayList of Facebook usernames used by the user
     * @param	ArrayList<String> groups	ArrayList of groups the user is in.
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

    public BasicDBObject getContactByName(String contactName)
    {
        BasicDBObject query = new BasicDBObject("name", contactName);
        DBCursor cursor = contacts.find(query);
        if (cursor.hasNext())
            return (BasicDBObject)cursor.next();
        else
            return null;
    }

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
            case "name":
            case "emails":
            case "facebook":
            case "groups":
            case "numbers":
                BasicDBObject change = new BasicDBObject("$set",
                        new BasicDBObject().append(updatedField, newValue));
                BasicDBObject query = this.getContactByName(contactName);
                contacts.update(query, change);

            return true;
        }

        System.out.println("Use the right 'updatedField' name");
        return false;

    }


    public void printContacts() {
        DBCursor cursor = contacts.find();

        while (cursor.hasNext()) {
            Contact c = new Contact();
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
                        System.out.println("Sending an email to " +
                                ((ArrayList<String>) curr.get("emails")).get(j));
                        //sendgrid.email((ArrayList<String>) curr.get("emails"))[j], message);
                    }
                }
                else if (methods.get(i).equals("facebook")) {
                    for (int j = 0; j < ((ArrayList<String>) curr.get("facebook")).size(); j++) {
                        System.out.println("Sending a private message to " +
                                ((ArrayList<String>) curr.get("facebook")).get(j));
                        //facebook.PM((ArrayList<String>) curr.get("FBusernames"))[j],
                        //  message);
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


    }

}
