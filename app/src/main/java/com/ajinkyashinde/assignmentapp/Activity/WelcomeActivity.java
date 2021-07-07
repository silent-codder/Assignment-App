package com.ajinkyashinde.assignmentapp.Activity;

/**
 Developed BY: Ajinkya Shinde
 Designation: Android Learner
 Date: 06/07/2021
 **/

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import com.ajinkyashinde.assignmentapp.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        findViewById(R.id.btnLogin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
            }
        });

        findViewById(R.id.btnSignUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WelcomeActivity.this,SignUpActivity.class));
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Check login details
        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        if (sharedPreferences.getBoolean("loggedIn",false)){
            startActivity(new Intent(WelcomeActivity.this,MainActivity.class));
            finish();
        }

    }
}