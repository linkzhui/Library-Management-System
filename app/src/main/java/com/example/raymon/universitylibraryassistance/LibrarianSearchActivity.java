package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LibrarianSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView editTextISBN;
    private ImageButton imageButtonSearch;
    private Button buttonUpdate;
    private Button buttonDelete;
    private DatabaseReference mDatabase;
    private ConstraintLayout cl;
    private EditText editTextPublisher;
    private EditText editTextAuthor;
    private EditText editTextYear;
    private EditText editTextLocation;
    private EditText editTextCopies;
    private EditText editTextStatus;
    private EditText editTextKeywords;
    final Catalog[] cat = new Catalog[1];
    String title = "";
    final String TAG = "Librarian_Search";
    private String book_title = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_search);
        editTextISBN = findViewById(R.id.editTextISBN);
        cl = findViewById(R.id.constraintLayout);
        imageButtonSearch = findViewById(R.id.imageButtonSearch);
        imageButtonSearch.setOnClickListener(this);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(this);
        buttonUpdate = findViewById(R.id.buttonReturn);
        buttonUpdate.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

        editTextPublisher = findViewById(R.id.editTextPublisher);
        editTextAuthor = findViewById(R.id.editTextAuthor);
        editTextYear = findViewById(R.id.editTextYear);
        editTextLocation = findViewById(R.id.editTextLocation);
        editTextCopies = findViewById(R.id.editTextCopies);
        editTextStatus = findViewById(R.id.editTextStatus);
        editTextKeywords = findViewById(R.id.editTextKeywords);
    }

    @Override
    public void onClick(View view) {


        if (view.getId() == R.id.imageButtonSearch && checkISBN()) {
            final String input = editTextISBN.getText().toString();
            title = input;
                if (isISBN(input)) {
                    Log.e(TAG,"find file through ISBN");
                    //the user's input is ISBN
                    mDatabase.child("Books").addListenerForSingleValueEvent(
                            //
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        Log.e("isbn",child.child("isbn_thirteen").getValue(String.class));
                                        if (child.hasChild("isbn_thirteen") && child.child("isbn_thirteen").getValue(String.class).equals(input)) {
                                            Log.e(TAG,"The book have been founded");

                                            String author = dataSnapshot.child(input).child("author").getValue(String.class);
                                            String call_number = dataSnapshot.child(input).child("call_number").getValue(String.class);
                                            String coverage_image = dataSnapshot.child(input).child("coverage_image").getValue(String.class);
                                            String current_status = dataSnapshot.child(input).child("current_status").getValue(String.class);
                                            String isbn_ten = dataSnapshot.child(input).child("isbn_ten").getValue(String.class);
                                            String isbn_thirteen = dataSnapshot.child(input).child("isbn_thirteen").getValue(String.class);
                                            String keywords = dataSnapshot.child(input).child("keywords").getValue(String.class);
                                            String location_in_the_library = dataSnapshot.child(input).child("location_in_the_library").getValue(String.class);
                                            String number_of_copies = dataSnapshot.child(input).child("number_of_copies").getValue(String.class);
                                            String publisher = dataSnapshot.child(input).child("publisher").getValue(String.class);
                                            String title = dataSnapshot.child(input).child("title").getValue(String.class);
                                            String year_of_publication = dataSnapshot.child(input).child("year_of_publication").getValue(String.class);
                                            cat[0] = new Catalog(author,title, call_number, publisher, year_of_publication, location_in_the_library, number_of_copies, current_status, keywords, coverage_image, isbn_thirteen, isbn_ten);
                                            //cat[0] = dataSnapshot.child(input).getValue(Catalog.class);
                                            setView(cat[0]);
                                            book_title = title;
                                            break;
                                        }
                                        if (child.hasChild("isbn_ten") && child.child("isbn_ten").getValue(String.class).equals(input)) {
                                            Log.e(TAG,"The book have been founded");
                                            String author = dataSnapshot.child(input).child("author").getValue(String.class);
                                            String call_number = dataSnapshot.child(input).child("call_number").getValue(String.class);
                                            String coverage_image = dataSnapshot.child(input).child("coverage_image").getValue(String.class);
                                            String current_status = dataSnapshot.child(input).child("current_status").getValue(String.class);
                                            String isbn_ten = dataSnapshot.child(input).child("isbn_ten").getValue(String.class);
                                            String isbn_thirteen = dataSnapshot.child(input).child("isbn_thirteen").getValue(String.class);
                                            String keywords = dataSnapshot.child(input).child("keywords").getValue(String.class);
                                            String location_in_the_library = dataSnapshot.child(input).child("location_in_the_library").getValue(String.class);
                                            String number_of_copies = dataSnapshot.child(input).child("number_of_copies").getValue(String.class);
                                            String publisher = dataSnapshot.child(input).child("publisher").getValue(String.class);
                                            String title = dataSnapshot.child(input).child("title").getValue(String.class);
                                            String year_of_publication = dataSnapshot.child(input).child("year_of_publication").getValue(String.class);
                                            cat[0] = new Catalog(author,title, call_number, publisher, year_of_publication, location_in_the_library, number_of_copies, current_status, keywords, coverage_image, isbn_thirteen, isbn_ten);
                                            //cat[0] = dataSnapshot.child(input).getValue(Catalog.class);
                                            setView(cat[0]);
                                            book_title = title;
                                            break;
                                        }
                                    }

                                    Toast.makeText(getApplicationContext(),"The book is not founded",Toast.LENGTH_SHORT).show();

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                } else {
                    Log.e(TAG,"find file through book title");
                    mDatabase.child("Books").addListenerForSingleValueEvent(
                            //
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if(dataSnapshot.hasChild(input))
                                    {
                                        Log.e(TAG,"The book have been founded");
                                        String author = dataSnapshot.child(input).child("author").getValue(String.class);
                                        String call_number = dataSnapshot.child(input).child("call_number").getValue(String.class);
                                        String coverage_image = dataSnapshot.child(input).child("coverage_image").getValue(String.class);
                                        String current_status = dataSnapshot.child(input).child("current_status").getValue(String.class);
                                        String isbn_ten = dataSnapshot.child(input).child("isbn_ten").getValue(String.class);
                                        String isbn_thirteen = dataSnapshot.child(input).child("isbn_thirteen").getValue(String.class);
                                        String keywords = dataSnapshot.child(input).child("keywords").getValue(String.class);
                                        String location_in_the_library = dataSnapshot.child(input).child("location_in_the_library").getValue(String.class);
                                        String number_of_copies = dataSnapshot.child(input).child("number_of_copies").getValue(String.class);
                                        String publisher = dataSnapshot.child(input).child("publisher").getValue(String.class);
                                        String title = dataSnapshot.child(input).child("title").getValue(String.class);
                                        String year_of_publication = dataSnapshot.child(input).child("year_of_publication").getValue(String.class);
                                        cat[0] = new Catalog(author,title, call_number, publisher, year_of_publication, location_in_the_library, number_of_copies, current_status, keywords, coverage_image, isbn_thirteen, isbn_ten);
                                        //cat[0] = dataSnapshot.child(input).getValue(Catalog.class);
                                        book_title = title;
                                        setView(cat[0]);
                                    }
                                    else{
                                        Toast.makeText(getApplicationContext(),"The book is not founded",Toast.LENGTH_SHORT).show();
                                    }

                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }

        }
        else if(view.getId() == R.id.buttonReturn) {
            //Toast.makeText(getApplicationContext(), "Update button press", Toast.LENGTH_SHORT).show();
            if (checkInput())
            {
                Map<String, Object> childUpdates = new HashMap<>();

                childUpdates.put("/Books/"+title+"/author/",editTextAuthor.getText().toString());
                childUpdates.put("/Books/"+title+"/current_status/",editTextStatus.getText().toString());
                childUpdates.put("/Books/"+title+"/keywords/",editTextKeywords.getText().toString());
                childUpdates.put("/Books/"+title+"/location_in_the_library/",editTextLocation.getText().toString());
                childUpdates.put("/Books/"+title+"/number_of_copies/",editTextCopies.getText().toString());
                childUpdates.put("/Books/"+title+"/publisher/",editTextPublisher.getText().toString());
                childUpdates.put("/Books/"+title+"/year_of_publication/",editTextYear.getText().toString());
                mDatabase.updateChildren(childUpdates);
                editTextISBN.setEnabled(true);
            }
        }
        else if(view.getId() == R.id.buttonDelete)
        {
            Log.e("book title",book_title);
            //Toast.makeText(getApplicationContext(),"Delete button press",Toast.LENGTH_SHORT).show();
            mDatabase.child("Books").child(book_title).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(!dataSnapshot.child("current_status").getValue(String.class).equals("IDLE"))
                    {
                        //current book is borrowed by someone, cannot delete yet
                        Toast.makeText(getApplicationContext(),"Cannot delete, this book still borrow by someone",Toast.LENGTH_SHORT).show();
                        cl.setVisibility(View.GONE);
                        editTextISBN.setEnabled(true);
                    }
                    else{
                        //Delete this book
                        mDatabase.child("Books").child(title).setValue(null);
                        cl.setVisibility(View.GONE);
                        editTextISBN.setEnabled(true);
                        Toast.makeText(getApplicationContext(),"Delete book successful!",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


        }
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
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        cl.setVisibility(View.INVISIBLE);
//        editTextISBN.setEnabled(true);
    }

    private boolean checkISBN(){
        if(TextUtils.isEmpty(editTextISBN.getText()))
        {
            editTextISBN.setError("Required.");
            return false;
        }
        return true;
    }
    private boolean checkInput(){
        boolean valied = true;
        if(TextUtils.isEmpty(editTextAuthor.getText()))
        {
            editTextAuthor.setError("Required.");
            valied = false;
        }
        if(TextUtils.isEmpty(editTextPublisher.getText()))
        {
            editTextAuthor.setError("Required.");
            valied = false;
        }
        if(TextUtils.isEmpty(editTextCopies.getText()))
        {
            editTextCopies.setError("Required");
            valied = false;
        }
        if(TextUtils.isEmpty(editTextKeywords.getText()))
        {
            editTextKeywords.setError("Required");
            valied = false;
        }
        if(TextUtils.isEmpty(editTextStatus.getText()))
        {
            editTextStatus.setError("Required");
            valied = false;
        }
        if(TextUtils.isEmpty(editTextYear.getText()))
        {
            editTextYear.setError("Required");
            valied = false;
        }
        if(TextUtils.isEmpty(editTextLocation.getText()))
        {
            editTextLocation.setError("Required");
            valied = false;
        }
        return valied;
    }

    private void setView(Catalog catlalog)
    {
        cl.setVisibility(View.VISIBLE);
        editTextISBN.setEnabled(false);
        editTextPublisher.setText(catlalog.getPublisher());
        editTextISBN.setText(catlalog.getTitle());
        editTextAuthor.setText(catlalog.getAuthor());
        editTextCopies.setText(catlalog.getNumber_of_copies()+"");
        editTextKeywords.setText(catlalog.getKeywords());
        editTextStatus.setText(catlalog.getCurrent_status());
        editTextYear.setText(catlalog.year_of_publication);
        editTextLocation.setText(catlalog.location_in_the_library+"");
    }

    private static boolean isISBN(String book) {
        try {
            Long.parseLong(book);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
