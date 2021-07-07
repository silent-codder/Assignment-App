package com.ajinkyashinde.assignmentapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.audiofx.BassBoost;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.ajinkyashinde.assignmentapp.Room.UserData;
import com.ajinkyashinde.assignmentapp.Room.UserDataBase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.content.ContentValues.TAG;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {


    EditText mFirstName, mLastName, mUserName, mPassword, mQuestionAnswer,mAddressLine;
    TextView mBirthDate;
    Calendar calendar;
    CircleImageView mProfileImg;
    Spinner mQuestionSpinner;
    int day, month, year;
    String lat,log;
    String Que, DOB = null;
    Button mBtnSubmit;
    Uri profileImgUri = null;
    UserDataBase userDataBase;

    String[] Questions = {"What is your favorite pet name?", "What is your birth place?", "Who is your favorite actor?"};

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile(
                    "(?=.*[A-Z])"         //at least 1 upper case letter
            );

    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mBirthDate = findViewById(R.id.birthDate);
        mQuestionSpinner = findViewById(R.id.spinner);
        mFirstName = findViewById(R.id.firstName);
        mLastName = findViewById(R.id.lastName);
        mUserName = findViewById(R.id.userName);
        mPassword = findViewById(R.id.password);
        mQuestionAnswer = findViewById(R.id.queAns);
        mBtnSubmit = findViewById(R.id.btnSubmit);
        mAddressLine = findViewById(R.id.address);
        mProfileImg = findViewById(R.id.userProfileImg);

        calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        setUpDB();

        mBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(SignUpActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        month += 1;
                        mBirthDate.setText(day + "-" + month + "-" + year);

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        mQuestionSpinner.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the question list
        ArrayAdapter questions = new ArrayAdapter(this, android.R.layout.simple_spinner_item, Questions);
        questions.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mQuestionSpinner.setAdapter(questions);

        mBtnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateFirstName() && validateLastName() && validateDOB() && validateUsername()
                && validatePassword() && validateAnswer() && validateProfileUri() && validateAddress()) {

                    SaveData();

                }
            }
        });


        //Select profile img
        mProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImg();
            }
        });

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }

    private void SaveData() {

        String firstName = mFirstName.getText().toString().trim();
        String lastName = mLastName.getText().toString().trim();
        String DOB = mBirthDate.getText().toString().trim();
        String userName = mUserName.getText().toString().trim();
        String password = mPassword.getText().toString().trim();
        String answer = mQuestionAnswer.getText().toString().trim();
        String address = mAddressLine.getText().toString().trim();
        String ProfileUrl = String.valueOf(profileImgUri);


        UserData userData = new UserData(firstName,lastName,userName,password,DOB,address,lat,log,Que,answer,ProfileUrl);
        userDataBase.dao().userInfo(userData);

        Toast.makeText(this, "Register successfully", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(SignUpActivity.this,LoginActivity.class));

        List<UserData> data = userDataBase.dao().getUserData();

        for (int i=0;i<data.size();i++){
            Log.d(TAG, "Users : " + data.get(i).getUserName() + data.get(i).getPassword());
        }

    }

    private void setUpDB(){
        userDataBase = Room.databaseBuilder(this,UserDataBase.class,"userDB")
                .allowMainThreadQueries().build();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(SignUpActivity.this,Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                getCurrentLocation();
            }else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION},44);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 44 && (grantResults.length>0) && (grantResults[0] + grantResults[1] == PackageManager.PERMISSION_GRANTED)){
            getCurrentLocation();
        }else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getApplication().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location!=null){
                        lat = String.valueOf(location.getLatitude());
                        log = String.valueOf(location.getLongitude());
                        Log.d(TAG, "long & lat : " + location.getLongitude() + " " + location.getLatitude() );
                    }else {
                        LocationRequest locationRequest = new LocationRequest()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(1000)
                                .setFastestInterval(1000)
                                .setNumUpdates(1);

                        LocationCallback locationCallback = new LocationCallback(){
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                super.onLocationResult(locationResult);

                                Location location1 = locationResult.getLastLocation();
                                Log.d(TAG, "long & lat : " + location1.getLongitude() + " " + location1.getLatitude() );
                            }
                        };

                        fusedLocationProviderClient.requestLocationUpdates(locationRequest,locationCallback, Looper.myLooper());
                    }
                }
            });
        }else {
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }
    }

    private boolean validateFirstName() {
        String firstName = mFirstName.getText().toString().trim();
        if (firstName.isEmpty()) {
            mFirstName.setError("Field can't be empty");
            return false;
        } else {
            mFirstName.setError(null);
            return true;
        }

    }

    private boolean validateLastName() {
        String lastName = mLastName.getText().toString().trim();
        if (lastName.isEmpty()) {
            mLastName.setError("Field can't be empty");
            return false;
        } else {
            mLastName.setError(null);
            return true;
        }

    }

    private boolean validateUsername() {
        String userName = mUserName.getText().toString().trim();
        if (userName.isEmpty()) {
            mUserName.setError("Field can't be empty");
            return false;
        } else if (userName.length() > 15) {
            mUserName.setError("Username too long");
            return false;
        } else {
            mUserName.setError(null);
            return true;
        }
    }
    private boolean validatePassword() {
        String password = mPassword.getText().toString().trim();
        if (password.isEmpty()) {
            mPassword.setError("Field can't be empty");
            return false;
        } else if (password.length()<8) {
            mPassword.setError("Password must be 8 digit long");
            return false;
        } else {
            mPassword.setError(null);
            return true;
        }
    }

    private boolean validateDOB() {
        String DOB = mBirthDate.getText().toString().trim();
        if (DOB.isEmpty()) {
            mBirthDate.setError("Select Birth Date");
            return false;
        } else {
            mBirthDate.setError(null);
            return true;
        }

    }

    private boolean validateAnswer() {
        String answer = mQuestionAnswer.getText().toString().trim();
        if (answer.isEmpty()) {
            mQuestionAnswer.setError("Field can't be empty");
            return false;
        } else {
            mQuestionAnswer.setError(null);
            return true;
        }

    }

    private boolean validateProfileUri() {
        String ImgUrl = String.valueOf(profileImgUri);
        if (ImgUrl.isEmpty()) {
            Toast toast = Toast.makeText(this, "Profile Image select", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER,0,0);
            toast.show();
            return false;
        } else {
            return true;
        }

    }

    private boolean validateAddress() {
        String address = mAddressLine.getText().toString().trim();
        if (address.isEmpty()) {
            mAddressLine.setError("Field can't be empty");
            return false;
        } else {
            mAddressLine.setError(null);
            return true;
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Que = Questions[position];
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void UploadImg() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality(40)
                .setAspectRatio(1,1)
                .start(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                profileImgUri = result.getUri();
                mProfileImg.setImageURI(profileImgUri);
                Log.d(TAG, "Img url: " + profileImgUri);

                File file = new File(String.valueOf(profileImgUri));
                long length = file.length();
                length = length/1024;
                Log.d(TAG, "File Path : " + file.getPath() + ", File size : " + length +" KB");

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getApplicationContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }

        }
    }
}