package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

public class BookSearchActivity extends AppCompatActivity implements View.OnClickListener{

    String username;
    ImageButton imageButtonSearch;
    private Button buttonBorrow;
    private DatabaseReference mDatabase;
    private ConstraintLayout cl;
    private EditText editTextISBN;
    final Catalog[] cat = new Catalog[1];
    public static LinkedList<Catalog> borrow_cart = new LinkedList<>();
    public static HashMap<String,Boolean> isSelected = new HashMap<>();
    private Button buttonWaitList;

    String title = "";
    final String TAG = "Book_Search";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        Intent intent = getIntent();
        username = intent.getStringExtra("UserID");
        cl = findViewById(R.id.constraintLayoutBook);
        imageButtonSearch = findViewById(R.id.imageButtonSearch);
        imageButtonSearch.setOnClickListener(this);
        editTextISBN = findViewById(R.id.editTextISBN);
        buttonBorrow = findViewById(R.id.buttonBorrow);
        buttonBorrow.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        buttonWaitList = findViewById(R.id.buttonWaitList);
        buttonWaitList.setOnClickListener(this);
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
                                        cat[0] = child.getValue(Catalog.class);
                                        setView(cat[0]);
                                        break;
                                    }
                                    if (child.hasChild("isbn_ten") && child.child("isbn_ten").getValue(String.class).equals(input)) {
                                        Log.e(TAG,"The book have been founded");
                                        cat[0] = child.getValue(Catalog.class);
                                        setView(cat[0]);
                                        break;
                                    }
                                }
                                cl.setVisibility(View.INVISIBLE);
                                buttonBorrow.setVisibility(View.INVISIBLE);
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
                                    setView(cat[0]);
                                }
                                else{
                                    cl.setVisibility(View.INVISIBLE);
                                    buttonBorrow.setVisibility(View.INVISIBLE);
                                    Toast.makeText(getApplicationContext(),"The book is not founded",Toast.LENGTH_SHORT).show();
                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
            }

        }
        else if(view.getId() == R.id.buttonBorrow)
        {

            if(cat[0].getCurrent_status().equals("IDLE") && !isSelected.containsKey(cat[0].getTitle()))
            {
                Toast.makeText(getApplicationContext(),"This book have been added into borrow cart successful",Toast.LENGTH_SHORT).show();
                borrow_cart.add(cat[0]);
                isSelected.put(cat[0].getTitle(),false);
            }
            else if(isSelected.containsKey(cat[0].getTitle()))
            {
                Toast.makeText(getApplicationContext(),"This book is already exist in the borrow cart",Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),"This book is already been borrowed by others",Toast.LENGTH_SHORT).show();
            }
            Log.e("size of List",borrow_cart.size()+"");
            buttonBorrow.setVisibility(View.INVISIBLE);
            buttonWaitList.setVisibility(View.INVISIBLE);
            cl.setVisibility(View.INVISIBLE);
            editTextISBN.setText("");

        }
        else if (view.getId() == R.id.buttonWaitList)
        {

            mDatabase.child("Books").child(cat[0].title).addListenerForSingleValueEvent(
                    //
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if(!dataSnapshot.child("borrowed_by").getValue(String.class).equals(username)){
                                if(!dataSnapshot.hasChild("waiting_list"))
                                {
                                    //if the waiting list for this book is empty
                                    DateFormat df = new SimpleDateFormat("MM/dd/yy");
                                    Date dateobj = new Date();
                                    mDatabase.child("Books").child(cat[0].title).child("waiting_list").child(username).setValue(df.format(dateobj));
                                    Toast.makeText(getApplicationContext(),"Add to the waiting list successful!",Toast.LENGTH_SHORT).show();


                                }
                                else{
                                    //check if current user is exist in the waiting list or not
                                    if(dataSnapshot.child("waiting_list").hasChild(username))
                                    {
                                        //user already exist in the waiting list
                                        Toast.makeText(getApplicationContext(),"you already exist in the waiting list",Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        //add user to the waiting list
                                        DateFormat df = new SimpleDateFormat("MM/dd/yy");
                                        Date dateobj = new Date();
                                        mDatabase.child("Books").child(cat[0].title).child("waiting_list").child(username).setValue(df.format(dateobj));
                                        Toast.makeText(getApplicationContext(),"Add to the waiting list successful!",Toast.LENGTH_SHORT).show();
                                    }

                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(),"You already borrowed this book, you cannot add yourself into the waiting list",Toast.LENGTH_SHORT).show();
                            }

                            buttonBorrow.setVisibility(View.INVISIBLE);
                            buttonWaitList.setVisibility(View.INVISIBLE);
                            cl.setVisibility(View.INVISIBLE);
                            editTextISBN.setText("");
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }


                    });


        }
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

    private void setView(Catalog catlalog)
    {
        cl.setVisibility(View.VISIBLE);
        if(catlalog.getCurrent_status().equals("IDLE"))
        {
            buttonBorrow.setVisibility(View.VISIBLE);
        }
        else{
            buttonWaitList.setVisibility(View.VISIBLE);
        }
        TextView title = findViewById(R.id.textViewBookTitle);
        TextView author = findViewById(R.id.textViewAuthor);
        TextView call_number = findViewById(R.id.textViewCallNumber);
        TextView publisher = findViewById(R.id.textViewPublisher);
        TextView year_publish = findViewById(R.id.textViewYear);
        TextView Location_in_lib = findViewById(R.id.textViewLocationInLib);
        TextView num_copies = findViewById(R.id.textViewCopies);
        TextView current_status = findViewById(R.id.textViewCurrentStatus);
        TextView key_words = findViewById(R.id.textViewKeyWords);
        ImageView image = findViewById(R.id.imageViewCoverage);
        title.setText(catlalog.getTitle());
        author.setText("Author: "+ catlalog.getAuthor());
        call_number.setText("Call Number:" + catlalog.getCall_number());
        publisher.setText("Publisher: "+ catlalog.getPublisher());
        year_publish.setText("Year publish: "+ catlalog.getYear_of_publication());
        Location_in_lib.setText("Location in the library: " + catlalog.getLocation_in_the_library());
        num_copies.setText("Number of copies: "+catlalog.getNumber_of_copies());
        current_status.setText("Current Status: "+catlalog.getCurrent_status());
        key_words.setText("Key Words: "+catlalog.getKeywords());

        //convert the string to bitmap
//            int size = book_list.get(i).coverage_image.size();
//            byte[] bitmapdata = new byte[size];
//            for(int j = 0;j<size;j++)
//            {
//                bitmapdata[j] = book_list.get(i).coverage_image.get(j);
//            }
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
        byte[] decodedString = Base64.decode(catlalog.getCoverage_image(), Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        image.setImageBitmap(decodedByte);
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

