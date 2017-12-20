package com.example.raymon.universitylibraryassistance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class BookReturnActivity extends AppCompatActivity implements ViewStub.OnClickListener {

    HashMap<String,Boolean> isSelected = new HashMap<>();
    private LinkedList<book> BorrowedBookList = new LinkedList<>();
    private ListView book_list;
    private Button buttonReturn;
    private int check_box_count = 0;
    ListViewAdapter adapter;
    private Button buttonRenew;
    //user ID:
    String username;
    private DatabaseReference mDatabase;
    String TAG = "BookReturnActivity";
    int total_borrow_book_count = 0;
    Date today = new Date();
    long new_date = today.getTime()+60*60*24*TestAssistanceActivity.offset[0];


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        Intent intent = getIntent();
        username = intent.getStringExtra("UserID");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_return);
        book_list = findViewById(R.id.listViewBookReturn);
        buttonReturn = findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(this);
        buttonRenew = findViewById(R.id.buttonRenew);
        buttonRenew.setOnClickListener(this);
        adapter = new ListViewAdapter(this);
        book_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.buttonReturn)
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
                    //return book add to List -> mark
                    mark.add(element);
                    check_box_count--;
                }
            }
            int return_book_count = mark.size();


            //return email confirmation
            //
            for(final book element:mark) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("Users").child(username).child("bookList").hasChild(element.title) && dataSnapshot.child("Books").hasChild(element.title)) {
                            Log.e("The book is founded","!!!!!!!!!!!!!!!!");
//                            mDatabase.child("Users").child(username).child("bookList").child(element.title).setValue(null);
//                            mDatabase.child("Books").child(element.title).child("current_status").setValue("IDLE");
//                            mDatabase.child("Books").child(element.title).child("borrowed_by").setValue("NULL");
                            //need to check if the user need to pay the fee or not
                            String dueDateString = dataSnapshot.child("Users").child(username).child("bookList").child(element.title).child("DueDate").getValue(String.class);
                            Date dueDate = parsingDateString(dueDateString);

                            if(dueDate.getTime()<new_date){
                                long diff = new_date - dueDate.getTime();
                                float days = (diff / (1000*60*60*24));
                                Integer fine = (int)days + 1;
                                makeToast("Fine: " + fine);
                            }
                                mDatabase.child("Users").child(username).child("bookList").child(element.title).setValue(null);
                                mDatabase.child("Books").child(element.title).child("current_status").setValue("IDLE");
                                mDatabase.child("Books").child(element.title).child("borrowed_by").setValue("NULL");
                                String useremail = dataSnapshot.child("Users").child(username).child("email").getValue(String.class);
                                String message = "You have succesfully return: " + element.title;
                                String subject = "Book Return Confirmation";
                                sendEmail(useremail,message,subject);

                                //if some one on the waiting list
                                // send email remainder and put him out of the waitinglist
                                if (dataSnapshot.child("Books").child(element.title).hasChild("waiting_list")) {
                                    Iterable<DataSnapshot> waitingUsers = dataSnapshot.child("Books").child(element.title).child("waiting_list").getChildren();
                                    String nextWaitingUser = waitingUsers.iterator().next().getKey();
                                    String waitingUserEmail = dataSnapshot.child("Users").child(nextWaitingUser).child("email").getValue(String.class);
                                    String waitingListSubject = "Your Book is Ready";
                                    String waitingListMessage = "Your Book: " + element.title + " is Ready";
                                    mDatabase.child("Books").child(element.title).child("waiting_list").child(nextWaitingUser).setValue(null);
                                    sendEmail(waitingUserEmail, waitingListMessage, waitingListSubject);
                                }


                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }

                });
                BorrowedBookList.remove(element);
                adapter.notifyDataSetChanged();
            }


            mDatabase.child("Users").child(username).child("num_of_borrowed_book").setValue(null);
            mDatabase.child("Users").child(username).child("num_of_borrowed_book").setValue(total_borrow_book_count-return_book_count);
        }
        else{

            Toast.makeText(this,"Extend Successful",Toast.LENGTH_SHORT).show();
            Log.e("size",BorrowedBookList.size()+"");
            // use mark_extend_books to update database
            final List<book> mark_extend_books = new LinkedList<>();

            for(book element:BorrowedBookList)
            {

                if (isSelected.get(element.title))
                {
                    //update the database
                    isSelected.remove(element.title);
                    //extend book add to List -> mark
                    mark_extend_books.add(element);
                    check_box_count--;
                }
            }
            int extend_book_count = mark_extend_books.size();

            for(final book element:mark_extend_books) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("Users").child(username).child("bookList").hasChild(element.title) && dataSnapshot.child("Books").hasChild(element.title)) {
                            Log.e("The book is founded", "!!!!!!!!!!!!!!!!");
                            //add 30 days to old borrow day
                            //String dueDateString = dataSnapshot.child("Users").child(username).child("bookList").child(element.title).child("DueDate").getValue(String.class);
                            Integer numberofRenew = dataSnapshot.child("Users").child(username).child("bookList").child(element.title).child("NumberOfRenew").getValue(Integer.class);
                            //int numberofRenew = Integer.parseInt(numberofRenewString);
                            if(numberofRenew >=2){
                                // make a toast
                                makeToast("Exceed the Renew Limit");
                            }else if(dataSnapshot.child("Books").child(element.title).hasChild("waiting_list")){
                                makeToast("Someone on WaitingList");
                            }else{
                                String oldDateString = dataSnapshot.child("Users").child(username).child("bookList").child(element.title).child("DueDate").getValue(String.class);
                                Date oldDate = parsingDateString(oldDateString);
                                Calendar c = Calendar.getInstance();
                                c.setTime(oldDate);
                                c.add(Calendar.DATE,30);
                                Date renewDate = c.getTime();
                                DateFormat df = new SimpleDateFormat("MM/dd/yy");

                                // update due date and time of renew in the database
                                mDatabase.child("Users").child(username).child("bookList").child(element.title).child("DueDate").setValue(df.format(renewDate));
                                mDatabase.child("Users").child(username).child("bookList").child(element.title).child("NumberOfRenew").setValue(numberofRenew + 1);

                                //send email for renew confirmation
                                String useremail = dataSnapshot.child("Users").child(username).child("email").getValue(String.class);
                                String booktitle = element.title;
                                String message = "You have succesfully extend book: " + booktitle;
                                String subject = "Book Extension Confirmation";
                                sendEmail(useremail,message,subject);
                                /***
                                 if (dataSnapshot.child("Books").child(element.title).child("waiting_list").hasChild(element.title)) {

                                 }
                                 ***/




                            }

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }


                });
                //BorrowedBookList.remove(element);
                //adapter.notifyDataSetChanged();

            }
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

    public void makeToast(String messagetoToast){
        Context context = getApplicationContext();
        CharSequence text = messagetoToast;
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
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

    private void sendEmail(String email, String message) {
        //Getting content for email
        //Creating SendMail object
        String subject = "Return Book Confirmation";
        EmailReturnConfirmation sm = new EmailReturnConfirmation(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
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
            init();
        }


        private void init(){
            mDatabase = FirebaseDatabase.getInstance().getReference();
            mDatabase.child("Users").child(username).child("bookList").addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    total_borrow_book_count=(int) snapshot.getChildrenCount();
                    for(DataSnapshot child:snapshot.getChildren())
                    {
                        String key = child.getKey();
                        Log.e("key",child.getKey());
                        String value = snapshot.child(key).child("BorrowDate").getValue(String.class);
                        Log.e("value",value);
                        boolean due = parsingDateString(value).getTime()<new_date;
                        book temp_book = new book(key,value,due);

                        BorrowedBookList.add(temp_book);
                        isSelected.put(key,false);
                    }
                    Log.e("List size",BorrowedBookList.size()+"");
                    //Log.e("snapshot",snapshot.getValue().toString());  //prints "Do you have data? You'll love Firebase."
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }

            });
        }

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
            ListViewAdapter.ViewHolder holder = null;
            String title = BorrowedBookList.get(position).title;
            String date = BorrowedBookList.get(position).date;
            LayoutInflater inflater = LayoutInflater.from(context);
            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.book_return_layout, null);
                holder = new ListViewAdapter.ViewHolder();
                holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.tvName = (TextView) convertView.findViewById(R.id.textViewBookTitle);
                holder.LL = (LinearLayout) convertView.findViewById(R.id.linear_layout_up);
                holder.date = (TextView) convertView.findViewById(R.id.textViewBorrowDate);
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
        Boolean due;
        public book(String title, String date, Boolean due)
        {
            this.title = title;
            this.date = date;
            this.due = due;
        }
    }
}
