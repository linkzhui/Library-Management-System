package com.example.raymon.universitylibraryassistance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class CirculationActivity extends AppCompatActivity implements View.OnClickListener {

    HashMap<String,Boolean> isSelected = new HashMap<>();
    private LinkedList<book> BorrowedBookList = new LinkedList<>();
    private DatabaseReference mDatabase;
    String TAG = "CirculationActivity";
    String username;
    private EditText editTextAccount;
    private ImageButton imageButtonSearch;
    private Button buttonBorrow;
    private Button buttonReturn;
    private Button buttonSearch;
    private ListView listView;
    ListViewAdapter adapter;
    private int total_borrow_book_count = 0;
    private int check_box_count = 0;
    final boolean[] found = new boolean[1];
    Date today = new Date();

    long new_date = today.getTime()+1000*60*60*24*TestAssistanceActivity.offset[0];

    protected void onResume() {

        super.onResume();

    }

    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        Log.e("previous day",today.getTime()+"");
        Log.e("offset day",new_date+"");
        super.onCreate(savedInstanceState);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_circulation);
        editTextAccount = findViewById(R.id.editTextAccount);
        imageButtonSearch = findViewById(R.id.imageButtonSearch);
        imageButtonSearch.setOnClickListener(this);
        listView = findViewById(R.id.ListViewBorrow);
        buttonBorrow = findViewById(R.id.buttonBorrow);
        buttonBorrow.setOnClickListener(this);
        buttonReturn = findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(this);
        buttonSearch= findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);
        adapter = new ListViewAdapter(this);
        listView.setAdapter(adapter);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
    }


    @Override
    public void onClick(View view) {
        if(view.getId()==R.id.imageButtonSearch && checkEmail())
        {

            mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot child:dataSnapshot.getChildren())
                    {
                        username = child.getKey();
                        if (child.child("email").getValue(String.class).equals(editTextAccount.getText().toString()))
                        {
                            total_borrow_book_count = (int) child.child("bookList").getChildrenCount();
                            for(DataSnapshot element:child.child("bookList").getChildren())
                            {
                                found[0] = true;
                                String key = element.getKey();
                                Log.e("key",element.getKey());
                                String value = element.child("DueDate").getValue(String.class);
                                Log.e("value",value);

                                boolean due = parsingDateString(value).getTime()<new_date;
                                long diff = parsingDateString(value).getTime() - new_date;
                                if(parsingDateString(value).getTime()<new_date)
                                {
                                    String message = "You need to return " + key + "! it is already pass the due day! the due day is: "+parsingDateString(value);
                                    String subject = "Warning! Book Return Pass the Due Day";
                                    sendEmail(child.child("email").getValue(String.class),message,subject);
                                }
                                else if (1<=diff && diff<=5){
                                    String message = "the book:" + key + " need to due in " + diff+ " day! the due day is: "+parsingDateString(value);
                                    String subject = "Warning! Book Due Soon";
                                    sendEmail(child.child("email").getValue(String.class),message,subject);
                                }
                                Log.e("boolean result",due+"");
                                Log.e("current time",new_date+"");
                                Log.e("due day",parsingDateString(value).getTime()+"");
                                book temp_book = new book(key,value,due);
                                BorrowedBookList.add(temp_book);
                                isSelected.put(key,false);

                            }
                            adapter.notifyDataSetChanged();
                            buttonBorrow.setVisibility(View.VISIBLE);
                            buttonReturn.setVisibility(View.VISIBLE);
                            buttonSearch.setVisibility(View.VISIBLE);

                        }
                    }
                    if(!found[0])
                    {
                        Toast.makeText(getApplicationContext(),"user name does not exist",Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });

        }
        else if(view.getId() == R.id.buttonSearch)
        {
            Intent intent = new Intent(getApplicationContext(),BookSearchActivity.class);
            intent.putExtra("UserID",username);
            startActivity(intent);
        }
        else if(view.getId()==R.id.buttonBorrow)
        {
            if(BookSearchActivity.borrow_cart.size() == 0)
            {
                Toast.makeText(getApplicationContext(),"Please click search button to select some books first",Toast.LENGTH_SHORT).show();
            }
            else{
                Intent intent = new Intent(getApplicationContext(),BookBorrowActivity.class);
                intent.putExtra("UserID",username);
                startActivity(intent);

            }
        }
        else if(view.getId()==R.id.buttonReturn)
        {
            Toast.makeText(this,"Check out successful",Toast.LENGTH_SHORT).show();
            Log.e("size",BorrowedBookList.size()+"");
            final List<book> mark = new LinkedList<>();
            for(book element:BorrowedBookList)
            {

                if (isSelected.get(element.title))
                {
                    //update the database
                    isSelected.remove(element.title);
                    mark.add(element);
                    check_box_count--;
                }
            }
            int return_book_count = mark.size();
            for(final book element:mark)
            {

                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("Users").child(username).child("bookList").hasChild(element.title) && dataSnapshot.child("Books").hasChild(element.title)) {
                            Log.e("The book is founded","!!!!!!!!!!!!!!!!");
                            mDatabase.child("Users").child(username).child("bookList").child(element.title).setValue(null);
                            mDatabase.child("Books").child(element.title).child("current_status").setValue("IDLE");
                            //need to check if the user need to pay the fee or not
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
                BorrowedBookList.remove(element);

            }
            adapter.notifyDataSetChanged();
            mDatabase.child("Users").child(username).child("num_of_borrowed_book").setValue(null);
            mDatabase.child("Users").child(username).child("num_of_borrowed_book").setValue(total_borrow_book_count-return_book_count);
        }
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

    private boolean checkEmail(){
        if(TextUtils.isEmpty(editTextAccount.getText()))
        {
            editTextAccount.setError("Required.");
            return false;
        }
        return true;
    }

    class ListViewAdapter extends BaseAdapter {

        private Context context;




        // 用来控制CheckBox的选中状况


        class ViewHolder {
            TextView tvName;
            CheckBox cb;
            LinearLayout LL;
            TextView date;
            TextView due;
        }

        public ListViewAdapter(Context context) {
            // TODO Auto-generated constructor stub
            this.context = context;
            BorrowedBookList = new LinkedList<>();
            //init();
        }


//        private void init(){
//            mDatabase = FirebaseDatabase.getInstance().getReference();
//            mDatabase.child("Users").child(username).child("bookList").addListenerForSingleValueEvent(new ValueEventListener(){
//                @Override
//                public void onDataChange(DataSnapshot snapshot) {
//                    total_borrow_book_count=(int) snapshot.getChildrenCount();
//                    for(DataSnapshot child:snapshot.getChildren())
//                    {
//                        String key = child.getKey();
//                        Log.e("key",child.getKey());
//                        String value = child.getValue(String.class);
//                        Log.e("value",value);
//                        book temp_book = new book(key,value);
//                        BorrowedBookList.add(temp_book);
//                        isSelected.put(key,false);
//                    }
//                    Log.e("List size",BorrowedBookList.size()+"");
//                    //Log.e("snapshot",snapshot.getValue().toString());  //prints "Do you have data? You'll love Firebase."
//
//                }
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//                }
//
//            });
//        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            Log.e("book return count",BorrowedBookList.size()+"");
            return BorrowedBookList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return BorrowedBookList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }



        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            // 页面
            Log.e("book return position",position+"");
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            ViewHolder holder = null;
            String title = BorrowedBookList.get(position).title;
            String date = BorrowedBookList.get(position).date;
            LayoutInflater inflater = LayoutInflater.from(context);
            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.book_return_layout, null);
                holder = new ListViewAdapter.ViewHolder();
                holder.cb =  convertView.findViewById(R.id.checkBox1);
                holder.tvName =  convertView.findViewById(R.id.textViewBookTitle);
                holder.LL =  convertView.findViewById(R.id.linear_layout_up);
                holder.date = convertView.findViewById(R.id.textViewBorrowDate);
                holder.due = (TextView) convertView.findViewById(R.id.textViewDue);
                convertView.setTag(holder);
            } else {
                // 取出holder
                holder = (ListViewAdapter.ViewHolder) convertView.getTag();
            }
            System.out.println(isSelected.toString());
            holder.tvName.setText(title);
            // 监听checkBox并根据原来的状态来设置新的状态
            holder.LL.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    System.out.println("点击："+position);
                    if (isSelected.get(BorrowedBookList.get(position).title)) {
                        isSelected.put(BorrowedBookList.get(position).title, false);
                        check_box_count--;

                    } else {
                        check_box_count++;
                        isSelected.put(BorrowedBookList.get(position).title, true);
                    }
                    Log.i("current count",check_box_count+"");
                    notifyDataSetChanged();
                }
            });

            // 根据isSelected来设置checkbox的选中状况
            holder.cb.setChecked(getIsSelected().get(BorrowedBookList.get(position).title));
            holder.date.setText(date);
//            if(BorrowedBookList.get(position).due)
//            {
//                holder.due.setVisibility(View.VISIBLE);
//            }
            return convertView;
        }

        public HashMap<String, Boolean> getIsSelected() {
            return isSelected;
        }



    }

    class book{
        String title;
        String date;
        boolean due;
        public book(String title, String date, boolean due)
        {
            this.title = title;
            this.date = date;
            this.due = due;
        }
    }
}
