package com.example.lessonplannerpro;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    TextView username;
    TextView password;
    String usernameStr;
    String passwordStr;

    Button loginBtn;
    Button signUpBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        firebaseAuth = FirebaseAuth.getInstance();

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.login); // setOnClickListener
        signUpBtn = findViewById(R.id.signUp); // onClick (xml)

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userLogin();
            }
        });


    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            // handle the already logged in user
            currentUser.reload();
        }
    }


    private boolean checkValid() {
        usernameStr = username.getText().toString().trim();
        passwordStr = password.getText().toString().trim();

        // checking if username and passwords are empty
        if (TextUtils.isEmpty(usernameStr) || TextUtils.isEmpty(passwordStr)) {
            Toast.makeText(this, "Fill all fields please", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }


    /****************************************** register ****************************************************/


    public void userSignUp(View view) {
        if (checkValid()) {
            // register process
            firebaseAuth.createUserWithEmailAndPassword(usernameStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //checking if success
                    if (task.isSuccessful()) {
                        //display some message here
                        Toast.makeText(LoginActivity.this, "Successfully registered", Toast.LENGTH_LONG).show();
                    } else {
                        //display some message here
                        Toast.makeText(LoginActivity.this, "Registration Error", Toast.LENGTH_LONG).show();
                    }

                }
            });
        }
    }

    /****************************************** login ****************************************************/


    public void userLogin() {
        if (checkValid()) {
            // login process
            firebaseAuth.signInWithEmailAndPassword(usernameStr, passwordStr).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    //if the task is successfully
                    if (task.isSuccessful()) {
                        //start the profile activity
                        finish();
                        startActivity(new Intent(getApplicationContext(), LessonDetails.class));

                    }
                }
            });
        }
    }


}