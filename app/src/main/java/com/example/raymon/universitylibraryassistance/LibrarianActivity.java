package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class LibrarianActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView textViewAccount;
    private Button buttonManage;
    private Button buttonCirculation;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_librarian);
        Intent intent = getIntent();
        username = intent.getStringExtra("UserID");
        textViewAccount = findViewById(R.id.textViewAccount);
        textViewAccount.setText(username);
        buttonManage = findViewById(R.id.buttonManage);
        buttonManage.setOnClickListener(this);
        buttonCirculation = findViewById(R.id.buttonCirculation);
        buttonCirculation.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.buttonManage)
        {
            //Toast.makeText(LibrarianActivity.this,"Manage",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),BookManagementActivity.class);
            intent.putExtra("UserID",username);
            startActivity(intent);
        }
        else if(view.getId() == R.id.buttonCirculation)
        {
            //Toast.makeText(LibrarianActivity.this,"Circulation",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(),CirculationActivity.class);
            intent.putExtra("UserID",username);
            startActivity(intent);
        }
    }
}
