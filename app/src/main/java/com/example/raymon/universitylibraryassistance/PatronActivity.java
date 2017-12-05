package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PatronActivity extends AppCompatActivity implements View.OnClickListener {

    private Button buttonSearch;
    private Button buttonBorrow;
    private Button buttonReturn;
    private TextView TextViewUsername;
    private String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patron);
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        TextViewUsername = findViewById(R.id.textViewAccount);
        TextViewUsername.setText(username);
        buttonBorrow = findViewById(R.id.buttonBorrow);
        buttonBorrow.setOnClickListener(this);
        buttonReturn = findViewById(R.id.buttonReturn);
        buttonReturn.setOnClickListener(this);
        buttonSearch = findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonBorrow)
        {
            Toast.makeText(PatronActivity.this,"Borrow",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.buttonReturn)
        {
            Toast.makeText(PatronActivity.this,"Return",Toast.LENGTH_SHORT).show();
        }
        else if(view.getId() == R.id.buttonSearch)
        {
            Toast.makeText(PatronActivity.this,"Search",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),BookSearchActivity.class);
            intent.putExtra("Username",username);
            startActivity(intent);
        }
    }
}
