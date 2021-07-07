package com.ajinkyashinde.assignmentapp.Activity;
/**
 Developed BY: Ajinkya Shinde
 Designation: Android Learner
 Date: 06/07/2021
 **/

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ajinkyashinde.assignmentapp.Fragment.HomeFragment;
import com.ajinkyashinde.assignmentapp.Fragment.ListFragment;
import com.ajinkyashinde.assignmentapp.Fragment.MapsFragment;
import com.ajinkyashinde.assignmentapp.R;
import com.ajinkyashinde.assignmentapp.Room.DAO;
import com.ajinkyashinde.assignmentapp.Room.UserData;
import com.ajinkyashinde.assignmentapp.Room.UserDataBase;
import com.google.android.material.navigation.NavigationView;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {

    NavigationView nav;
    ActionBarDrawerToggle toggle;
    DrawerLayout drawerLayout;
    Fragment selectFragment;
    String UserName,ProfileUrl;
    UserDataBase userDataBase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        nav = findViewById(R.id.navMenu);
        drawerLayout = findViewById(R.id.drawer);
        toggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.Open,R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initialize Database
        setUpDB();

        // get data from local db
        fetchData();

        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                removeColor(nav);

                item.setChecked(true);

                switch (item.getItemId()){
                    case R.id.home :
                        selectFragment = new HomeFragment();
                        break;
                    case R.id.list :
                        selectFragment = new ListFragment();
                        break;
                    case R.id.map :
                        selectFragment = new MapsFragment();
                        break;
                    case R.id.logout :
                        logOut();
                        break;
                }

                if (selectFragment != null){
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectFragment).addToBackStack(null).commit();
                }
                drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
        });
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new HomeFragment()).commit();

    }


    private void fetchData() {
        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
        final DAO dao = userDataBase.dao();
        UserData userData = dao.login(sharedPreferences.getString("username",null),sharedPreferences.getString("password",null));
        UserName = userData.getFirstName() + " " + userData.getLastName();
        ProfileUrl = userData.getProfileUrl();
        View view = nav.inflateHeaderView(R.layout.header_layout);
        CircleImageView profile = view.findViewById(R.id.userProfileImg);
        TextView name = view.findViewById(R.id.userName);
        name.setText(UserName);
        Picasso.get().load(ProfileUrl).into(profile);
    }

    private void logOut() {

        new AlertDialog.Builder(this)
                .setIcon(R.mipmap.ic_launcher)
                .setTitle("Assignment App")
                .setMessage("Are you sure to logout ?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SharedPreferences sharedPreferences = getSharedPreferences("Login",MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.clear();
                        editor.commit();
                        Toast.makeText(getApplicationContext(), "LogOut Successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this,WelcomeActivity.class));
                        finish();
                        moveTaskToBack(true);
                    }

                })
                .setNegativeButton("No", null)
                .show();


    }


    private void removeColor(NavigationView view) {
        for (int i = 0; i < view.getMenu().size(); i++) {
            MenuItem item = view.getMenu().getItem(i);
            item.setChecked(false);
        }
    }

    private void setUpDB(){
        userDataBase = Room.databaseBuilder(this, UserDataBase.class,"userDB")
                .allowMainThreadQueries().build();
    }
}