package com.example.raymon.universitylibraryassistance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class BookSearchActivity extends AppCompatActivity implements View.OnClickListener{

    private String username;
    private static EditText editTextInput;
    private Button buttonSearch;
    private static BookListAdapter adapter;
    public static ArrayList<Catalog> book_list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_search);
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        editTextInput = findViewById(R.id.editTextInput);
        ListView listView = findViewById(R.id.ListViewBook);
        adapter = new BookListAdapter(this);
        listView.setAdapter(adapter);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Life cycle test", "We are at onResume()");
//        adapter.notifyDataSetChanged();
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
            Log.e("", "onPostExecute(Result result) called");
            Log.e("booklist size",book_list.size()+"");
            adapter.notifyDataSetChanged();
            Log.e("finish notify","");
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
            {
                json.append(tmp).append("\n");
            }
            reader.close();
            JSONObject data = new JSONObject(json.toString());
            Log.e("jsonobject data",data.toString());
            parseJSON(data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void parseJSON(JSONObject jsonObject)
    {
        Log.e("Life cycle","parse the JSON");
        Catalog catalog;
        String title = "";
        String author = "";
        String call_number = "";
        String publisher = "";
        String year_of_publication = "";
        String keywords = "";
        Bitmap coverage_image = null;
        try {
            JSONArray book_array = jsonObject.getJSONArray("items");
            for(int i = 0;i<Math.min(book_array.length(),3);i++) {
                JSONObject temp_book = book_array.getJSONObject(i).getJSONObject("volumeInfo");
                if (temp_book.has("title"))
                {
                    title = temp_book.getString("title");
                    Log.i("title",title);
                }

                //parse the author
                String prefix = "";
                if(temp_book.has("authors"))
                {
                    for(int j = 0;j<temp_book.getJSONArray("authors").length();j++)
                    {
                        author+=prefix+temp_book.getJSONArray("authors").get(j);
                        prefix = " ";

                    }
                }
                Log.i("author",author);

                //parse the publisher
                if(temp_book.has("publisher"))
                {
                    publisher = temp_book.getString("publisher");
                }
                Log.i("publisher",publisher);

                //parse the year of publication
                if(temp_book.has("publishedDate"))
                {
                    year_of_publication = temp_book.getString("publishedDate");
                    year_of_publication = year_of_publication.substring(0,year_of_publication.indexOf("-"));
                }
                Log.i("Year of Publication",year_of_publication);

                keywords = editTextInput.getText().toString();
                Log.i("Key words",keywords);

                Bitmap image = null;
                if(temp_book.getJSONObject("imageLinks").has("smallThumbnail"))
                {
                    String image_url = temp_book.getJSONObject("imageLinks").getString("smallThumbnail");
                    try {
                        InputStream in = new java.net.URL(image_url).openStream();
                        image = BitmapFactory.decodeStream(in);
                    } catch (Exception e) {
                        Log.i("Error", e.getMessage());
                        e.printStackTrace();
                    }
                    coverage_image = image;

                }
                Log.i("coverage_image",coverage_image.toString());
                catalog = new Catalog(author,call_number,publisher,year_of_publication,keywords,coverage_image);
                catalog.setTitle(title);
                book_list.add(catalog);
            }
        } catch (JSONException e) {
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

    class BookListAdapter extends BaseAdapter {

        Context context;
        public BookListAdapter(Context context)
        {
            this.context = context;
        }
        @Override
        public int getCount() {
            Log.e("current size",book_list.size()+"");
            return book_list.size();
        }

        @Override
        public Object getItem(int i) {
            return book_list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            Log.e("set view",i+"");
            if(convertView == null)
            {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.book_item,parent,false);
            }
            TextView title = convertView.findViewById(R.id.textViewBookTitle);
            TextView author = convertView.findViewById(R.id.textViewAuthor);
            TextView call_number = convertView.findViewById(R.id.textViewCallNumber);
            TextView publisher = convertView.findViewById(R.id.textViewPublisher);
            TextView year_publish = convertView.findViewById(R.id.textViewYear);
            TextView Location_in_lib = convertView.findViewById(R.id.textViewLocationInLib);
            TextView num_copies = convertView.findViewById(R.id.textViewCopies);
            TextView current_status = convertView.findViewById(R.id.textViewCurrentStatus);
            TextView key_words = convertView.findViewById(R.id.textViewKeyWords);
            ImageView image = convertView.findViewById(R.id.imageViewCoverage);
            title.setText("Title: " + book_list.get(i).title);
            author.setText("Author: "+ book_list.get(i).author);
            call_number.setText("Call Number" + book_list.get(i).call_number);
            publisher.setText("Publisher: "+ book_list.get(i).publisher);
            year_publish.setText("Year publish: "+ book_list.get(i).year_of_publication);
            Location_in_lib.setText("Location in the library: " + book_list.get(i).location_in_the_library);
            num_copies.setText("Number of copies: "+book_list.get(i).number_of_copies);
            current_status.setText("Current Status: "+book_list.get(i).current_status);
            key_words.setText("Key Words: "+book_list.get(i).keywords);
            image.setImageBitmap(book_list.get(i).coverage_image);
            return convertView;
        }
    }
}

