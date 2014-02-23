package com.example.mailmate.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    EditText to;
    EditText subject;
    EditText msg;

    SendMail sendMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendMail = new SendMail();

        to = (EditText)findViewById(R.id.email);
        subject = (EditText)findViewById(R.id.subject);
        msg = (EditText)findViewById(R.id.msg);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public void sendNow(View v) {
        Toast toast = Toast.makeText(getApplicationContext(), "Message sent to " + to.getText().toString(), Toast.LENGTH_LONG);
        toast.show();

        String to = this.to.getText().toString();
        String subject = this.subject.getText().toString();
        String msg = this.msg.getText().toString();



        SendMail.send(to, subject, msg);


    }

}
