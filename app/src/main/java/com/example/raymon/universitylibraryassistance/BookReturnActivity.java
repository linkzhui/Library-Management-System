package com.example.raymon.universitylibraryassistance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

    //user ID:
    String username;
    private DatabaseReference mDatabase;
    String TAG = "BookReturnActivity";
    int total_borrow_book_count = 0;



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

        adapter = new ListViewAdapter(this);
        book_list.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    @Override
    public void onClick(View view) {
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

            //String useremail;
            //String[] booklist = new String[mark.size()];

            //return email confirmation
            for(final book element:mark) {
                mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child("Users").child(username).child("bookList").hasChild(element.title) && dataSnapshot.child("Books").hasChild(element.title)) {
                            Log.e("The book is founded","!!!!!!!!!!!!!!!!");
                            mDatabase.child("Users").child(username).child("bookList").child(element.title).setValue(null);
                            mDatabase.child("Books").child(element.title).child("current_status").setValue("IDLE");
                            mDatabase.child("Books").child(element.title).child("borrowed_by").setValue("NULL");
                            //need to check if the user need to pay the fee or not
                            String useremail = dataSnapshot.child("Users").child(username).child("email").getValue(String.class);
                            String booktitle = element.title;
                            sendEmail(useremail,booktitle);

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
                        String value = child.getValue(String.class);
                        Log.e("value",value);
                        book temp_book = new book(key,value);
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
            return convertView;
        }

        public HashMap<String, Boolean> getIsSelected() {
            return isSelected;
        }

    }

    class book{
        String title;
        String date;
        public book(String title, String date)
        {
            this.title = title;
            this.date = date;
        }
    }
}
