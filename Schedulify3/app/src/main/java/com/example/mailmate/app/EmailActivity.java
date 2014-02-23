package com.example.mailmate.app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class EmailActivity extends Activity {

    EditText to;
    EditText subject;
    EditText msg;

    SendMail sendMail;

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);
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

    public void selectContacts(View v) {
        Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        super.startActivityForResult(i, 1001);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1001:

                if (resultCode == Activity.RESULT_OK) {

                    Cursor s = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                            null, null, null);

                    String phoneNum = s.getString(s.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    Toast.makeText(getBaseContext(), phoneNum, Toast.LENGTH_LONG).show();
                    to.setText(phoneNum);
                    to.setHint("");

                }

                break;

        }

    }

}
