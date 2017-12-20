package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class TestAssistanceActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonSet;
    private TextView mDayLater;
    private EditText mDaysSet;
    public static long[] offset = new long[1];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_assistance);
        offset[0] = 0;
        mButtonSet = findViewById(R.id.buttonSetTest);
        mDayLater = findViewById(R.id.textDaysLater);
        mDaysSet = findViewById(R.id.editTextDate);
        mButtonSet.setOnClickListener(this);
        mDaysSet.setText(offset[0]+"");
    }

    public void onResume(View view)
    {
        super.onResume();
        mDaysSet.setText(offset[0]+"");
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
    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.buttonSetTest) {
            if(TextUtils.isEmpty(mDaysSet.getText()))
            {
                //if it is register option, then we need to check if the University ID Field is null or not
                mDaysSet.setError("Required.");
            }
            else{
                String days = mDaysSet.getText().toString();
                mDayLater.setText(days + " days from now.");
                offset[0] = Long.parseLong(days);
                Log.e("offest",offset[0]+"");
                Intent intent = new Intent(getApplicationContext(),LibrarianActivity.class);
                Toast.makeText(this,"Set the test days successful",Toast.LENGTH_SHORT).show();
                startActivity(intent);
            }

        }
    }


}
