package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LibrarianCustomAddActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText mTitle;
    private EditText mISBN;
    private EditText mAuthor;
    private EditText mPublisher;
    private EditText mLocation;
    private EditText mYear;
    private EditText mCopy;
    private EditText mStatus;
    private EditText mKeyword;
    private Button mButtonAdd;
    private static DatabaseReference mDatabase;
    private Catalog catalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_libraian_custom_add);
        mTitle = findViewById(R.id.editTextTitle);
        mISBN = findViewById(R.id.editTextISBN);
        mAuthor = findViewById(R.id.editTextAuthor);
        mPublisher = findViewById(R.id.editTextPublisher);
        mLocation = findViewById(R.id.editTextLocation);
        mYear = findViewById(R.id.editTextYear);
        mCopy = findViewById(R.id.editTextCopies);
        mStatus = findViewById(R.id.editTextStatus);
        mKeyword = findViewById(R.id.editTextKeywords);
        mButtonAdd = findViewById(R.id.buttonAdd);

        mButtonAdd.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();


    }



    @Override
    public void onClick(View view) {

        if(validateForm()){
            return;
        }
        final String title = mTitle.getText().toString();
        String author = mAuthor.getText().toString();
        String call_number = "";
        String publisher = mPublisher.getText().toString();
        String year_of_publication = mYear.getText().toString();
        String keywords = mKeyword.getText().toString();
        String coverage_image = "";
        String Current_status = "IDLE";
        String ISBN_13 = mISBN.getText().toString();
        String ISBN_10 = "";
        String location = mLocation.getText().toString();
        String copies = mCopy.getText().toString();


        catalog = new Catalog(author, title, call_number, publisher, year_of_publication, location, copies, Current_status,keywords, coverage_image,  ISBN_13,ISBN_10);



        mDatabase.child("Books").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        mDatabase.child("Books").child(title).setValue(catalog);
                        Toast.makeText(getApplicationContext(),"Add this book to database successful",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private boolean validateForm() {

        boolean isInvalid = false;
        if (mTitle.getText().toString().equals("")){
            mTitle.setError("Book title is required");
            isInvalid = true;
        }
        if (mAuthor.getText().toString().equals("")){
            mAuthor.setError("Author is required");
            isInvalid = true;
        }
        if (mISBN.getText().toString().equals("")){
            mISBN.setError("ISBN is required");
            isInvalid = true;
        }
        if (mPublisher.getText().toString().equals("")){
            mPublisher.setError("Publisher is required");
            isInvalid = true;
        }
        if (mKeyword.getText().toString().equals("")){
            mKeyword.setError("Keyword is required");
            isInvalid = true;
        }
        if (mYear.getText().toString().equals("")){
            mYear.setError("Year of publish is required");
            isInvalid = true;
        }
        if (mKeyword.getText().toString().equals("")){
            mKeyword.setError("Keyword is required");
            isInvalid = true;
        }
        if (mLocation.getText().toString().equals("")){
            mLocation.setError("Location is required");
            isInvalid = true;
        }
        if (mCopy.getText().toString().equals("")){
            mCopy.setError("Number of copies is required");
            isInvalid = true;
        }

        return isInvalid;
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
}
