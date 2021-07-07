package com.ajinkyashinde.assignmentapp.Fragment;

/**
 Developed BY: Ajinkya Shinde
 Designation: Android Learner
 Date: 06/07/2021
 **/

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ajinkyashinde.assignmentapp.Adapter.UserListAdapter;
import com.ajinkyashinde.assignmentapp.R;
import com.ajinkyashinde.assignmentapp.Room.DAO;
import com.ajinkyashinde.assignmentapp.Room.UserData;
import com.ajinkyashinde.assignmentapp.Room.UserDataBase;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class HomeFragment extends Fragment {

    UserDataBase userDataBase;
    TextView mFullName,mUserName,mBirthDate,mAddress;
    CircleImageView mProfileImg;
    Calendar calendar;
    ImageView mBtnEditProfile;
    SharedPreferences sharedPreferences;
    int day,month,year;
    Uri profileImgUri;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mUserName = view.findViewById(R.id.userName);
        mFullName = view.findViewById(R.id.fullName);
        mBirthDate = view.findViewById(R.id.birthDate);
        mAddress = view.findViewById(R.id.address);
        mBtnEditProfile = view.findViewById(R.id.editProfile);
        mProfileImg = view.findViewById(R.id.userProfileImg);
        calendar = Calendar.getInstance();

        mBtnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UploadImg();
            }
        });

        // Initialize Database
        setUpDB();

        // Get user data
        fetchData();

        view.findViewById(R.id.btnUpdateProfile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               showBottomSheet();
            }
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void fetchData() {
        sharedPreferences = getActivity().getSharedPreferences("Login",MODE_PRIVATE);
        final DAO dao = userDataBase.dao();
        UserData userData = dao.login(sharedPreferences.getString("username",null),sharedPreferences.getString("password",null));
        String Url = userData.getProfileUrl();
        if (!Url.isEmpty()) {
            Picasso.get().load(userData.getProfileUrl()).into(mProfileImg);
        }
        mUserName.setText(userData.getUserName());
        mFullName.setText(userData.getFirstName() + " " +userData.getLastName());
        mBirthDate.setText("DOB : " + userData.getBirthDate());
        mAddress.setText("Address : " + userData.getAddress());
    }

    private void showBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext());
        bottomSheetDialog.setContentView(R.layout.update_bottom_sheet);
        EditText mFirstName = bottomSheetDialog.findViewById(R.id.firstName);
        EditText mLastName = bottomSheetDialog.findViewById(R.id.lastName);
        EditText mUserName = bottomSheetDialog.findViewById(R.id.userName);
        EditText mAddress = bottomSheetDialog.findViewById(R.id.address);
        TextView mBirthDate = bottomSheetDialog.findViewById(R.id.birthDate);
        Button mUpdate = bottomSheetDialog.findViewById(R.id.btnUpdateProfile);
        final DAO dao = userDataBase.dao();
        UserData userData = dao.login(sharedPreferences.getString("username",null),sharedPreferences.getString("password",null));

        mAddress.setText(userData.getAddress());
        mBirthDate.setText(userData.getBirthDate());
        mFirstName.setText(userData.getFirstName());
        mLastName.setText(userData.getLastName());
        mUserName.setText(userData.getUserName());

        mBirthDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {

                        month += 1;
                        mBirthDate.setText(day + "-" + month + "-" + year);

                    }
                }, year, month, day);
                datePickerDialog.show();
            }
        });

        mUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String firstName = mFirstName.getText().toString().trim();
                String lastName = mLastName.getText().toString().trim();
                String DOB = mBirthDate.getText().toString().trim();
                String userName = mUserName.getText().toString().trim();
                String address = mAddress.getText().toString().trim();

                SharedPreferences sharedPreferences = getContext().getSharedPreferences("Login",MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username",userName);
                editor.putBoolean("loggedIn",true);
                editor.apply();
                editor.commit();
               userDataBase.dao().update(firstName,lastName,userName,DOB,address,sharedPreferences.getString("password",null));
               Toast.makeText(getActivity(), "Update successfully", Toast.LENGTH_SHORT).show();
               fetchData();
               bottomSheetDialog.cancel();
            }
        });

        bottomSheetDialog.findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.cancel();
            }
        });
        bottomSheetDialog.show();
    }

    private void setUpDB(){
        userDataBase = Room.databaseBuilder(getContext(), UserDataBase.class,"userDB")
                .allowMainThreadQueries().build();
    }

    private void UploadImg() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .setOutputCompressQuality(40)
                .setAspectRatio(1,1)
                .start(getContext(),this);
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

                // get image size
                calculateFileSize(profileImgUri);
                String Url = String.valueOf(profileImgUri);
                userDataBase.dao().updateProfile(Url,sharedPreferences.getString("password",null));
                Toast.makeText(getContext(), "Update Profile Image", Toast.LENGTH_SHORT).show();
                fetchData();

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(getContext(), "Error : " + error, Toast.LENGTH_SHORT).show();
            }

        }
    }

    public String calculateFileSize(Uri filepath)
    {
        File file = new File(filepath.getPath());

        // Get length of file in bytes
        long fileSizeInBytes = file.length();

        // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
        double fileSizeInKB = fileSizeInBytes / 1024;

        String calString=Double.toString(fileSizeInKB);
        Toast.makeText(getContext(), "Image Size is: " + calString + " KB", Toast.LENGTH_SHORT).show();
        return calString;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                new AlertDialog.Builder(getContext())
                        .setIcon(R.mipmap.ic_launcher)
                        .setTitle("Assignment App")
                        .setMessage("Are you sure to exit ?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                getActivity().finish();
                                getActivity().moveTaskToBack(true);
                            }

                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }
}