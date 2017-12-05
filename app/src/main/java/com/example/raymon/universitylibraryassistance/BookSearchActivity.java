package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class BookSearchActivity extends AppCompatActivity implements View.OnClickListener{

    private String username;
    private EditText editTextInput;
    private Button buttonSearch;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        editTextInput = findViewById(R.id.editTextInput);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        new LoadBookList().execute(editTextInput.getText().toString());
    }

    private static class LoadBookList extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... book) {
            Log.e("begin asyntask", "in asynctask cycle");
            getBookList(book[0]);
            return null;
        }


        @Override
        protected void onPostExecute(Void voids) {
            Log.i("", "onPostExecute(Result result) called");
            //adapter.notifyDataSetChanged();
        }

    }

    private static void getBookList(String book)
    {
        String GoogleBookApi = "https://www.googleapis.com/books/v1/volumes?q=";
        if(isISBN(book))
        {
            //the user input is ISBN
            GoogleBookApi+="isbn:";
        }
        else{
            //parse the book title, replace the " " in the book title with "%20"
            book.replace(" ","%20");
        }
        GoogleBookApi+=book;
        try {
            URL url = new URL(GoogleBookApi);
            Log.e("this is url",url.toString());
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuffer json = new StringBuffer(2048);
            String tmp="";
            while((tmp=reader.readLine())!=null)
                json.append(tmp).append("\n");
            reader.close();
            JSONObject data = new JSONObject(json.toString());
//            if(data.getInt("cod") != 200){
//                return null;
//            }
            Log.e("jsonobject data",data.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
