package com.example.raymon.universitylibraryassistance;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class BookBorrowActivity extends AppCompatActivity implements View.OnClickListener {

    private ListView book_list;
    private Button buttonBorrow;
    private int check_box_count = 0;
    private HashMap<String, Boolean> isSelected = BookSearchActivity.isSelected;
    ListViewAdapter adapter;
    String username;
    private DatabaseReference mDatabase;
    String TAG = "BookBorrowActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_borrow);
        book_list = findViewById(R.id.listViewBookBorrow);
        buttonBorrow = findViewById(R.id.buttonBorrow);
        buttonBorrow.setOnClickListener(this);
        adapter = new ListViewAdapter(this);
        book_list.setAdapter(adapter);
        Intent intent = getIntent();
        username = intent.getStringExtra("UserID");
        mDatabase = FirebaseDatabase.getInstance().getReference();



    }

    @Override
    public void onClick(View view) {

        final int[] current_borrowed_book = new int[1];
        mDatabase.child("Users").child(username).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        current_borrowed_book[0] = user.num_of_borrowed_book;
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
        if(check_box_count >Math.min(3, 9-current_borrowed_book[0]))
        {
            Toast.makeText(this,"Please select less than 4 items each time",Toast.LENGTH_SHORT).show();
        }
        else{
            int j = 0;
            Toast.makeText(this,"Check out successful",Toast.LENGTH_SHORT).show();
            Log.e("size",BookSearchActivity.borrow_cart.size()+"");
            final List<Catalog> mark = new LinkedList<>();
            for(Catalog element:BookSearchActivity.borrow_cart)
            {
                Log.e("j =",j+"");
                if (isSelected.get(element.title))
                {
                    j++;
                    //update the database
                    isSelected.remove(element.title);
                    mark.add(element);
                    check_box_count--;
                }
            }

            current_borrowed_book[0]+=mark.size();
            for(final Catalog element:mark)
            {

                mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.child(username).child("bookList").hasChild(element.title)) {
                            Log.e(TAG,"the book already existed in the database");
                        } else {
                            // put username as key to set value
                            DateFormat df = new SimpleDateFormat("MM/dd/yy");
                            Date dateobj = new Date();
                            mDatabase.child("Users").child(username).child("bookList").child(element.title).setValue(df.format(dateobj));
                            Log.i(TAG,"Store the book catalog into the database");
                            Map<String, Object> childUpdates = new HashMap<>();
                            User user = dataSnapshot.child(username).getValue(User.class);
                            int current_borrowed_book = user.num_of_borrowed_book;
                            childUpdates.put("/Books/"+element.title+"/current_status/","Borrow");
                            childUpdates.put("/Users/"+username+"/num_of_borrowed_book/",current_borrowed_book+1);
                            mDatabase.updateChildren(childUpdates);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }

                });
                BookSearchActivity.borrow_cart.remove(element);
            }


            adapter.notifyDataSetChanged();
        }
    }

    class ListViewAdapter extends BaseAdapter {

        private Context context;
        private LinkedList<Catalog> beans;


        // 用来控制CheckBox的选中状况



        class ViewHolder {
            TextView tvName;
            CheckBox cb;
            LinearLayout LL;
        }

        public ListViewAdapter(Context context) {
            // TODO Auto-generated constructor stub
            this.context = context;
            this.beans = BookSearchActivity.borrow_cart;

        }

        private void inital_data(){
            mDatabase.child("Users").child(username).child("book_list").addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());  //prints "Do you have data? You'll love Firebase."
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }

            });
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return beans.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return beans.get(position);
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
            Log.v("MyListViewBase", "getView " + position + " " + convertView);
            ViewHolder holder = null;
            String bean = beans.get(position).title;
            LayoutInflater inflater = LayoutInflater.from(context);
            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.book_borrow_layout, null);
                holder = new ViewHolder();
                holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.tvName = (TextView) convertView.findViewById(R.id.tv_device_name);
                holder.LL = (LinearLayout) convertView.findViewById(R.id.linear_layout_up);
                convertView.setTag(holder);
            } else {
                // 取出holder
                holder = (ViewHolder) convertView.getTag();
            }
            holder.tvName.setText(bean);
            // 监听checkBox并根据原来的状态来设置新的状态
            holder.LL.setOnClickListener(new View.OnClickListener() {

                public void onClick(View v) {
                    System.out.println("点击："+position);

                    if (isSelected.get(beans.get(position).title)) {
                        isSelected.put(beans.get(position).title, false);
                        check_box_count--;

                    } else {
                        check_box_count++;
                        isSelected.put(beans.get(position).title, true);
                    }
                    Log.i("current count",check_box_count+"");
                    notifyDataSetChanged();
                }
            });

            // 根据isSelected来设置checkbox的选中状况
            holder.cb.setChecked(getIsSelected().get(beans.get(position).title));
            return convertView;
        }

        public HashMap<String, Boolean> getIsSelected() {
            return isSelected;
        }

    }
}
