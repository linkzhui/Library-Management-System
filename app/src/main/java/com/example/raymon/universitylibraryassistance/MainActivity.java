package com.example.raymon.universitylibraryassistance;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mUniversityField;
    private FirebaseAuth mAuth;
    private Button mRegistrationButton;
    private Button mLoginButton;
    private DatabaseReference mDatabase;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mEmailField = findViewById(R.id.editTextEmail);
        mPasswordField = findViewById(R.id.editTextPassword);
        mUniversityField = findViewById(R.id.editTextUniversityID);
        mRegistrationButton = findViewById(R.id.Register);
        mLoginButton = findViewById(R.id.Login);

        Log.e("Life cycle test", "We are at onCreate()");
        mRegistrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(), "Begin registration", Toast.LENGTH_SHORT).show();
                createAccount(mEmailField.getText().toString(),mPasswordField.getText().toString());
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(mEmailField.getText().toString(),mPasswordField.getText().toString());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);

    }


    //Create a account for Librarian or patron,
    //the Librarian can only register use edu email account
    //the patron can register use any email account
    private void createAccount(final String email, String password) {
        Log.d(TAG, "createAccount:" + email);
        if (!validateForm(false)) {
            return;
        }
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            sendEmailVerification();
                            Log.d(TAG, "Please check the verification email");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user.getUid())) {
                                        Toast.makeText(getBaseContext(), "username is already registered, please change one", Toast.LENGTH_SHORT).show();
                                    } else {
                                        User new_user = new User(email,mUniversityField.getText().toString());
                                        // put username as key to set value
                                        mDatabase.child("Users").child(user.getUid()).setValue(new_user);
                                        Toast.makeText(getBaseContext(), "Successfully registered", Toast.LENGTH_SHORT).show();
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }

                            });
                            updateUI(user);
                        }
                        else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    //SignIn use Email as username and password
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm(true)) {
            return;
        }
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            FirebaseUser user = mAuth.getCurrentUser();
                            if(user.isEmailVerified())
                            {
                                Toast.makeText(MainActivity.this, "signInWithEmail:success",
                                        Toast.LENGTH_SHORT).show();
                                Log.d(TAG, "signInWithEmail:success");
                                if(LibrarianOrNot(user.getEmail()))
                                {
                                    Intent intent = new Intent(getApplicationContext(),LibrarianActivity.class);
                                    intent.putExtra("UserID",user.getUid());
                                    startActivity(intent);
                                }
                                else{
                                    Intent intent = new Intent(getApplicationContext(),PatronActivity.class);
                                    intent.putExtra("UserID",user.getUid());
                                    startActivity(intent);
                                }

                            }
                            else{
                                Toast.makeText(MainActivity.this, "signInWithEmail:failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(MainActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();

                            updateUI(null);
                        }


                    }
                });
        // [END sign_in_with_email]
    }


    //the input argument for this function is used to determine if the SignIn or Register is valid or not
    //the boolean variable SignIn, if the option is SignIn then boolean value is true,
    //if it is false, then it is Register option
    private boolean validateForm(boolean SignIn) {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        String universityID = mUniversityField.getText().toString();
        if(!SignIn && TextUtils.isEmpty(universityID))
        {
            //if it is register option, then we need to check if the University ID Field is null or not
            mUniversityField.setError("Required.");
            valid = false;
        }
        return valid;
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
        mAuth.signOut();
        updateUI(null);
        Toast.makeText(MainActivity.this,
                "Logout successful",
                Toast.LENGTH_SHORT).show();
        return true;
    }

    private void updateUI(FirebaseUser user)
    {
        if (user != null) {
            mUniversityField.setText("");
        } else {
            mEmailField.setText("");
            mPasswordField.setText("");
            mUniversityField.setText("");
        }
    }


    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(MainActivity.this,
                                    "Failed to send verification email.",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private boolean LibrarianOrNot(String input)
    {
        if(input.indexOf("@sjsu.edu")!=-1)
        {
            //!= -1 means we find @sjsu.edu in the string
            return true;
        }
        else{
            return false;
        }
    }

}
