package com.example.raymon.universitylibraryassistance;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class TestAssistanceActivity extends AppCompatActivity implements View.OnClickListener{

    private Button mButtonSet;
    private TextView mDayLater;
    private EditText mDaysSet;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_assistance);

        mButtonSet = findViewById(R.id.buttonSetTest);
        mDayLater = findViewById(R.id.textDaysLater);
        mDaysSet = findViewById(R.id.editTextDate);
        mButtonSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        if(view.getId() == R.id.buttonSetTest) {
            String days = mDaysSet.getText().toString();
            mDayLater.setText(days + " days from now.");
            mDaysSet.setText("");
        }
    }


}
