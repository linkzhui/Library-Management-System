package com.example.raymon.universitylibraryassistance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

public class LibrarianSearchActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView editTextISBN;
    private ImageButton imageButtonSearch;
    private Button buttonUpdate;
    private Button buttonDelete;
    private DatabaseReference mDatabase;
    final String TAG = "Librarian_Search";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_search);
        editTextISBN = findViewById(R.id.editTextISBN);
        imageButtonSearch = findViewById(R.id.imageButtonSearch);
        imageButtonSearch.setOnClickListener(this);
        buttonDelete = findViewById(R.id.buttonDelete);
        buttonDelete.setOnClickListener(this);
        buttonUpdate = findViewById(R.id.buttonUpdate);
        buttonUpdate.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onClick(View view) {

        final Catalog[] cat = new Catalog[1];
        if (view.getId() == R.id.imageButtonSearch) {
            final String input = editTextISBN.getText().toString();
            if (TextUtils.isEmpty(input)) {
                editTextISBN.setError("Required.");
            } else {
                if (isISBN(input)) {
                    Log.e(TAG,"find file through ISBN");
                    //the user's input is ISBN
                    mDatabase.child("Books").addListenerForSingleValueEvent(
                            //
                            new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                                        if (child.hasChild("ISBN_13") && child.child("ISBN_13").getValue(String.class) == input) {
                                            cat[0] = child.getValue(Catalog.class);
                                            break;
                                        }
                                        if (child.hasChild("ISBN_10") && child.child("ISBN_10").getValue(String.class) == input) {
                                            cat[0] = child.getValue(Catalog.class);
                                            break;
                                        }
                                    }
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
                                        Log.e(TAG,"!!!!!!!!!!!!!");
                                        cat[0] = dataSnapshot.getValue(Catalog.class);
                                    }
//                                    for(DataSnapshot child:dataSnapshot.getChildren())
//                                    {
//                                        Log.e("value",child.child("title").getValue(String.class).trim());
//                                        Log.e("string length",child.child("title").getValue(String.class).trim().length()+"");
//                                        if(child.child("title").getValue(String.class).trim().equals(input))
//                                        {
//
//                                        }
//                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });
                }
                if (cat[0] != null) {
                    Toast.makeText(getApplicationContext(), "The book is founded!", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(), "The book is not founded!", Toast.LENGTH_SHORT).show();
                }

            }
        }
    }


    private static boolean isISBN(String book) {
        try {
            Integer.parseInt(book);
        } catch (NumberFormatException e) {
            return false;
        }
        return true;
    }
}
