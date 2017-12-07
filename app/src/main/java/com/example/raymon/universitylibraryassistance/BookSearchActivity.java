package com.example.raymon.universitylibraryassistance;

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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class BookSearchActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton imageButtonSearch;
    private Button buttonBorrow;
    private DatabaseReference mDatabase;
    private ConstraintLayout cl;
    private EditText editTextISBN;
    final Catalog[] cat = new Catalog[1];
    public static LinkedList<Catalog> borrow_cart = new LinkedList<>();
    public static HashMap<String,Boolean> isSelected = new HashMap<>();
    String title = "";
    final String TAG = "Book_Search";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        cl = findViewById(R.id.constraintLayoutBook);
        imageButtonSearch = findViewById(R.id.imageButtonSearch);
        imageButtonSearch.setOnClickListener(this);
        editTextISBN = findViewById(R.id.editTextISBN);
        buttonBorrow = findViewById(R.id.buttonBorrow);
        buttonBorrow.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();
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
                                    cat[0] = dataSnapshot.child(input).getValue(Catalog.class);
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
        buttonBorrow.setVisibility(View.VISIBLE);
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

