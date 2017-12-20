package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class PatronActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSearch;
    private Button buttonCheckOut;
    private Button buttonReturn;
    private TextView TextViewUsername;
    private String username;
    private DatabaseReference mDatabase;
    Date today = new Date();
    long new_date = today.getTime()+1000*60*60*24*TestAssistanceActivity.offset[0];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patron);
        Intent intent = getIntent();
        username = intent.getStringExtra("UserID");
        TextViewUsername = findViewById(R.id.textViewAccount);
        TextViewUsername.setText(username);
        buttonCheckOut = findViewById(R.id.buttonCheckOut);
        buttonCheckOut.setOnClickListener(this);
        buttonReturn = findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(this);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("Users").child(username).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot child:snapshot.child("bookList").getChildren())
                {
                    String key = child.getKey();
                    Log.e("key",child.getKey());
                    String value = child.child("DueDate").getValue(String.class);
                    Log.e("value",value);

                    long diff = parsingDateString(value).getTime() - new_date;
                    if(parsingDateString(value).getTime()<new_date)
                    {
                        String message = "You need to return " + key + "! it is already pass the due day! the due day is: "+parsingDateString(value);
                        String subject = "Warning! Book Return Pass the Due Day";
                        sendEmail(snapshot.child("email").getValue(String.class),message,subject);
                    }
                    else if (1<=diff && diff<=5){
                        String message = "the book:" + key + " need to due in " + diff+ " day! the due day is: "+parsingDateString(value);
                        String subject = "Warning! Book Due Soon";
                        sendEmail(snapshot.child("email").getValue(String.class),message,subject);
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

        });



    }
    //create options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.setting, menu);
        return true;
    }

    //response to the menu item select
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch(item.getItemId()){
            case android.R.id.home:
                finish();
                break;
            default:
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                System.out.print("hi");
                startActivity(intent);
                Toast.makeText(this, "Logout successful", Toast.LENGTH_SHORT).show();
        }
        return true;
    }

    private void sendEmail(String email, String message, String subject) {
        //Getting content for email
        //Creating SendMail object
        EmailReturnConfirmation sm = new EmailReturnConfirmation(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }

    private Date parsingDateString(String dueDateString) {
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yy");
        try {
            Date dueDate = formatter.parse(dueDateString);
            return dueDate;

        } catch (ParseException e) {
            System.err.println("Could not parse date: " + dueDateString);
            return null;
        }
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonCheckOut)
        {
            if(BookSearchActivity.borrow_cart.size() == 0)
            {
                Toast.makeText(PatronActivity.this,"Please go to search area, select some books first",Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(getApplicationContext(),BookBorrowActivity.class);
                intent.putExtra("UserID",username);
                startActivity(intent);
            }
        }
        else if(view.getId() == R.id.buttonReturn)
        {
            //Toast.makeText(PatronActivity.this,"Return",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),BookReturnActivity.class);
            intent.putExtra("UserID",username);
            startActivity(intent);
        }
        else if(view.getId() == R.id.buttonSearch)
        {
            //Toast.makeText(PatronActivity.this,"Search",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),BookSearchActivity.class);
            intent.putExtra("UserID",username);
            startActivity(intent);
        }
    }
}
