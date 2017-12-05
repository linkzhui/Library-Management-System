package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PatronActivity extends AppCompatActivity implements View.OnClickListener{

    private Button button_search;
    private Button button_borrow;
    private Button button_return;
    private TextView textView_account;
    String username;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patron);
        Intent intent = getIntent();
        username = intent.getStringExtra("Username");
        textView_account = findViewById(R.id.textViewAccount);
        textView_account.setText(username);

        //button set onClickListener
        button_borrow = findViewById(R.id.buttonBorrow);
        button_borrow.setOnClickListener(this);
        button_search = findViewById(R.id.buttonSearchBook);
        button_search.setOnClickListener(this);
        button_return = findViewById(R.id.buttonReturn);
        button_return.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int ID = view.getId();
        switch (ID)
        {
            case R.id.buttonBorrow:
            {
                Toast.makeText(PatronActivity.this, "Button Borrow Pressed",
                        Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.buttonSearchBook:
            {
                Toast.makeText(PatronActivity.this, "Button Search Pressed",
                        Toast.LENGTH_SHORT).show();
                Intent myIntent = new Intent(PatronActivity.this, SearchActivity.class);
                myIntent.putExtra("Username",username);
                startActivity(myIntent);
                break;
            }
            case R.id.buttonReturn:
            {
                Toast.makeText(PatronActivity.this, "Button Return Pressed",
                        Toast.LENGTH_SHORT).show();
                break;
            }
        }

    }
}
