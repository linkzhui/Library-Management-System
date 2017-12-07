package com.example.raymon.universitylibraryassistance;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
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

public class LibrarianAddActivity extends AppCompatActivity implements View.OnClickListener{

    private String username;
    private static EditText editTextInput;
    private Button buttonSearch;
    private static BookListAdapter adapter;
    public static ArrayList<Catalog> book_list = new ArrayList<>();
    private static DatabaseReference mDatabase;
    private static String TAG = "LibrarianBookAddActivity";
    public static HashMap<String, Boolean> isSelected = new HashMap<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian_add);
        Intent intent = getIntent();
        username = intent.getStringExtra("UserID");
        editTextInput = findViewById(R.id.editTextInput);

        //set the adapter for the ListView
        ListView listView = findViewById(R.id.ListViewBook);
        adapter = new BookListAdapter(this);
        listView.setAdapter(adapter);

        buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);
        mDatabase = FirebaseDatabase.getInstance().getReference();


        //register the context menu for ListView
        registerForContextMenu(listView);

        // Enable the Up button
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo)
    {

        if(v.getId()==R.id.ListViewBook)
        {
            menu.add("Add this book");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        mDatabase.child("Books").addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild(book_list.get(info.position).title))
                        {
                            Toast.makeText(getApplicationContext(),"this book already exist in the library",Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mDatabase.child("Books").child(book_list.get(info.position).title).setValue(book_list.get(info.position));
                            Toast.makeText(getApplicationContext(),"Add this book to database successful",Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e("Life cycle test", "We are at onResume()");
        book_list = new ArrayList<>();
        //adapter.notifyDataSetChanged();
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
            Log.e("bookList size",book_list.size()+"");
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
            Log.e("JSONObject data",data.toString());
            parseJSON(data);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private static void parseJSON(JSONObject jsonObject) throws JSONException {
        Log.e("Life cycle","parse the JSON");
        Catalog catalog;

        JSONArray book_array = jsonObject.getJSONArray("items");
        book_list = new ArrayList<>();
        for(int i = 0;i<Math.min(book_array.length(),5);i++) {

            final String[] Current_status = {"IDLE"};
            String title = "NULL";

            String call_number = "NULL";
            String publisher = "NULL";
            String year_of_publication = "NULL";
            String keywords;
            String coverage_image = "";

            JSONObject temp_book = book_array.getJSONObject(i).getJSONObject("volumeInfo");
            if (temp_book.has("title"))
            {
                title = temp_book.getString("title");
            }

            String ISBN_13 = "";
            String ISBN_10 = "";
            if (temp_book.has("industryIdentifiers")) {
                String[] array = new String[2];
                for(int j = 0;j<temp_book.getJSONArray("industryIdentifiers").length();j++)
                {
                    array[j] = temp_book.getJSONArray("industryIdentifiers").getJSONObject(j).getString("identifier");
                }
                ISBN_13 = array[0];
                ISBN_10 = array[1];
            }

            //parse the author
            String prefix = "";
            String author = "NULL";
            if(temp_book.has("authors"))
            {
                author = "";
                for(int j = 0;j<temp_book.getJSONArray("authors").length();j++)
                {
                    author+=prefix+temp_book.getJSONArray("authors").get(j);
                    prefix = " ";
                }
            }

            //parse the publisher
            if(temp_book.has("publisher"))
            {
                publisher = temp_book.getString("publisher");
            }

            //parse the year of publication
            if(temp_book.has("publishedDate"))
            {
                year_of_publication = temp_book.getString("publishedDate");
                if(year_of_publication.contains("-"))
                {
                    year_of_publication = year_of_publication.substring(0,year_of_publication.indexOf("-"));
                }
            }

            keywords = editTextInput.getText().toString();

            Bitmap image = null;
            if(temp_book.getJSONObject("imageLinks").has("smallThumbnail"))
            {
                String image_url = temp_book.getJSONObject("imageLinks").getString("thumbnail");
                try {
                    InputStream in = new java.net.URL(image_url).openStream();
                    image = BitmapFactory.decodeStream(in);
                } catch (Exception e) {
                    Log.i("Error", e.getMessage());
                    e.printStackTrace();
                }

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] byteArray = stream.toByteArray();
                coverage_image = Base64.encodeToString(byteArray, Base64.DEFAULT);

            }

            //create catalog for this book, and add the catalog to ArrayList for bookListAdapter and FireBase database
            catalog = new Catalog(author,call_number,publisher,year_of_publication,keywords,coverage_image, Current_status[0],ISBN_13,ISBN_10);
            catalog.setTitle(title);
            book_list.add(catalog);
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
            title.setText(book_list.get(i).title);
            author.setText("Author: "+ book_list.get(i).author);
            call_number.setText("Call Number:" + book_list.get(i).call_number);
            publisher.setText("Publisher: "+ book_list.get(i).publisher);
            year_publish.setText("Year publish: "+ book_list.get(i).year_of_publication);
            Location_in_lib.setVisibility(View.GONE);
            num_copies.setVisibility(View.GONE);
            current_status.setVisibility(View.GONE);
            key_words.setText("Key Words: "+book_list.get(i).keywords);

            //convert the string to bitmap
//            int size = book_list.get(i).coverage_image.size();
//            byte[] bitmapdata = new byte[size];
//            for(int j = 0;j<size;j++)
//            {
//                bitmapdata[j] = book_list.get(i).coverage_image.get(j);
//            }
//            Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapdata, 0, bitmapdata.length);
            byte[] decodedString = Base64.decode(book_list.get(i).coverage_image, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            image.setImageBitmap(decodedByte);
            return convertView;
        }
    }
}
