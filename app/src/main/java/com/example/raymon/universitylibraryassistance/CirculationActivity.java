package com.example.raymon.universitylibraryassistance;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.LinkedList;

public class CirculationActivity extends AppCompatActivity {

    HashMap<String,Boolean> isSelected = new HashMap<>();
    private DatabaseReference mDatabase;
    String TAG = "CirculationActivity";
    String username;
    private EditText editTextAccount;
    private ImageButton imageButtonSearch;
    private Button buttonBorrow;
    private Button buttonReturn;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_circulation);
        editTextAccount = findViewById(R.id.editTextAccount);
        imageButtonSearch = findViewById(R.id.imageButtonSearch);
        listView = findViewById(R.id.ListViewBorrow);
        buttonBorrow = findViewById(R.id.buttonBorrow);
        buttonReturn = findViewById(R.id.buttonReturn);
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
                        BookReturnActivity.book temp_book = new BookReturnActivity.book(key,value);
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
            BookReturnActivity.ListViewAdapter.ViewHolder holder = null;
            String title = BorrowedBookList.get(position).title;
            String date = BorrowedBookList.get(position).date;
            LayoutInflater inflater = LayoutInflater.from(context);
            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.book_return_layout, null);
                holder = new BookReturnActivity.ListViewAdapter.ViewHolder();
                holder.cb = (CheckBox) convertView.findViewById(R.id.checkBox1);
                holder.tvName = (TextView) convertView.findViewById(R.id.textViewBookTitle);
                holder.LL = (LinearLayout) convertView.findViewById(R.id.linear_layout_up);
                holder.date = (TextView) convertView.findViewById(R.id.textViewBorrowDate);
                convertView.setTag(holder);
            } else {
                // 取出holder
                holder = (BookReturnActivity.ListViewAdapter.ViewHolder) convertView.getTag();
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
