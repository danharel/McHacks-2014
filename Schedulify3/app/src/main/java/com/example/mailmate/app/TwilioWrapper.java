package com.example.mailmate.app; /**
 * Created by Edmund on 2/22/14.
 */
import java.util.*;
import java.util.jar.Attributes;

import com.twilio.sdk.resource.instance.Account;
import com.twilio.sdk.TwilioRestClient;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

public class TwilioWrapper {

    //Constants for our testing
    private static final String ID = "ACed06be596231f943e0c0a6296fc79804";
    private static final String AUTH = "3835eeed479c590c25a29102f4342a94";
    private static final String SENDER = "6463628737";

    //Brian's
//    private static final String ID = "AC8a09e216a4dd7c05ccb9d7890a680657";
//    private static final String AUTH = "358eb46e50a2ceab7bf87eb51bc914f4";
//    private static final String SENDER = "+17185772891";

    //Twilio API classes
    private TwilioRestClient client;
    private Account account;
    private MessageFactory messageFactory;

    //List<NameValuePair> is a message, so this is a LinkedList
    //of the messages.
    private LinkedList<List<NameValuePair>> messages;

    public TwilioWrapper()
    {
        client = new TwilioRestClient(ID, AUTH);
        account = client.getAccount();
        messageFactory = account.getMessageFactory();

        messages = new LinkedList<List<NameValuePair>>();
    }

    public void addMessage(String recipientNumber, String body)
    {
        List<NameValuePair> msg = new ArrayList<NameValuePair>();
        msg.add(new BasicNameValuePair("To", recipientNumber));
        msg.add(new BasicNameValuePair("From", SENDER));
        msg.add(new BasicNameValuePair("Body", body));

        messages.add(msg);
    }

    public void sendBatch()
    {
        //Iterates through all messages, tries to send each.
        for (List<NameValuePair> msg : messages)
        {
            try {
                Message sms = messageFactory.create(msg);
            }
            catch (TwilioRestException e)
            {
                System.out.println("Failed to send message! GG");
            }
            //NEED TO PAUSE FOR A WHILE TO SEND MESSAGES WITHOUT BANHAMMER
        }
    }

}
