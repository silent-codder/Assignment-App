package com.ajinkyashinde.assignmentapp.Activity;
/**
    Developed BY: Ajinkya Shinde
    Designation: Android Learner
    Date: 06/07/2021
 **/

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ajinkyashinde.assignmentapp.R;
import com.ajinkyashinde.assignmentapp.Room.DAO;
import com.ajinkyashinde.assignmentapp.Room.UserData;
import com.ajinkyashinde.assignmentapp.Room.UserDataBase;

public class LoginActivity extends AppCompatActivity {

    EditText mUserName,mPassword;
    Button mBtnLogin;
    UserDataBase userDataBase;
    TextView mNewUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserName = findViewById(R.id.userName);
        mPassword = findViewById(R.id.password);
        mBtnLogin = findViewById(R.id.btnLogin);
        mNewUser = findViewById(R.id.newUser);

        // initialize database
        setUpDB();

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userName = mUserName.getText().toString().trim();
                String password = mPassword.getText().toString().trim();

                if (TextUtils.isEmpty(userName)){
                    mUserName.setError("UserName");
                }else if (TextUtils.isEmpty(password)){
                    mPassword.setError("Password");
                }else {
                    setUpDB();
                    final DAO dao = userDataBase.dao();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            UserData userData = dao.login(userName,password);
                            if (userData==null){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(LoginActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("username",userName);
                                editor.putString("password",password);
                                editor.putBoolean("loggedIn",true);
                                editor.apply();
                                editor.commit();
                                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                finish();
                            }
                        }
                    }).start();

                }
            }
        });

        mNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignUpActivity.class));
            }
        });
    }

    private void setUpDB(){
        userDataBase = Room.databaseBuilder(this, UserDataBase.class,"userDB")
                .allowMainThreadQueries().build();
    }
}