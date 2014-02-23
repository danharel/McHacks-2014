//import com.github.sendgrid.SendGrid;

/**
 * Created by Brian on 2/22/14.

public class SendMail {

    public static void send(final String to, final String subject, final String msg) {

        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {

                    SendGrid sendgrid = new SendGrid("brianyang", "McHacks_2014");

                    sendgrid.addTo(to);
                    sendgrid.setFrom("test@example.com");
                    sendgrid.setSubject(subject);
                    sendgrid.setText(msg);

                    sendgrid.send();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();


    }
}

 */
