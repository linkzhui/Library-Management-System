package com.example.raymon.universitylibraryassistance;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mbuttonSearch;
    private EditText mInputField;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mbuttonSearch = findViewById(R.id.buttonSearchBook);
        mbuttonSearch.setOnClickListener(this);
        mInputField = findViewById(R.id.editTextBook);

    }

    @Override
    public void onClick(View view) {
        new GetBookInfo().execute(mInputField.getText().toString());
    }


    private class GetBookInfo extends AsyncTask<String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... book) {
            Log.e("AsyncTask","Begin load the book info");
            try {
                GoogleBookApi(book[0]);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void voids) {
            Log.i("", "onPostExecute(Result result) called");
            //adapter.notifyDataSetChanged();
        }

    }

    private static void GoogleBookApi(String book) throws IOException, JSONException {
        Log.i("GoogleBookApi lifecycle",book);
        String Google_book_api = "https://www.googleapis.com/books/v1/volumes?";
        if(BookTitleOrNot(book))
        {
            Log.e("This is:","book");
            //the user's input is book's title
            book.replace(" ","%20");
            Google_book_api+="q="+book;

        }
        else{
            //the user's input is book's ISBN
            Log.e("This is:","ISBN");
            Google_book_api+="q=isbn:"+book;
        }
        URL url = new URL(Google_book_api);
        Log.i("The Google Book Api",url.toString());
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()));
        StringBuffer json = new StringBuffer(2048);
        String tmp="";
        while((tmp=reader.readLine())!=null)
            json.append(tmp).append("\n");
        reader.close();
        JSONObject data = new JSONObject(json.toString());
        Log.e("json object data",data.toString());

    }

    private static boolean BookTitleOrNot(String input)
    {
        try
        {
            //the input is ISBN
            Integer.parseInt(input);
        }
        catch(NumberFormatException ex)
        {
            //the input is book's title
            return true;
        }
        return false;
    }
}
