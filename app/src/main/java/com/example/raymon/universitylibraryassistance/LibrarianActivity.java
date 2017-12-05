package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LibrarianActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textView_account;
    private Button button_manage;
    private Button button_circulation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian);
        Intent intent = getIntent();
        String username = intent.getStringExtra("Username");
        textView_account = findViewById(R.id.textViewAccount);
        textView_account.setText(username);
        button_manage = findViewById(R.id.buttonManage);
        button_manage.setOnClickListener(this);
        button_circulation = findViewById(R.id.buttonCirculation);
        button_circulation.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonManage)
        {
            Toast.makeText(LibrarianActivity.this,"manage button press",Toast.LENGTH_SHORT).show();
            return;
        }
        if(view.getId() == R.id.buttonCirculation)
        {
            Toast.makeText(LibrarianActivity.this,"circulation button press",Toast.LENGTH_SHORT).show();
            return;
        }
    }
}
