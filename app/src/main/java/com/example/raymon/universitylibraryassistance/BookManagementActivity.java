package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class BookManagementActivity extends AppCompatActivity implements View.OnClickListener {

    private Button mButtonSearch;
    private Button mButtonAdd;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_management);
        Intent intent = getIntent();
        username = intent.getStringExtra("");
        mButtonAdd = findViewById(R.id.buttonAdd);
        mButtonAdd.setOnClickListener(this);
        mButtonSearch = findViewById(R.id.buttonSearch);
        mButtonSearch.setOnClickListener(this);
        mButtonSearch.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonAdd)
        {
            Toast.makeText(this,"u press the add button",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BookManagementActivity.this,LibrarianAddActivity.class);
            intent.putExtra("UserID",username);
            startActivity(intent);
        }
        if(view.getId() == R.id.buttonSearch)
        {
            Toast.makeText(this,"u press the add button",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(BookManagementActivity.this,LibrarianSearchActivity.class);
            intent.putExtra("UserID",username);
            startActivity(intent);
        }
    }
}
