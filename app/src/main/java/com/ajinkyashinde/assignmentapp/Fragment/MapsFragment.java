package com.ajinkyashinde.assignmentapp.Fragment;

/**
 Developed BY: Ajinkya Shinde
 Designation: Android Learner
 Date: 07/08/2021
 **/

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.ajinkyashinde.assignmentapp.Activity.SignUpActivity;
import com.ajinkyashinde.assignmentapp.R;
import com.ajinkyashinde.assignmentapp.Room.DAO;
import com.ajinkyashinde.assignmentapp.Room.UserData;
import com.ajinkyashinde.assignmentapp.Room.UserDataBase;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import static android.content.ContentValues.TAG;
import static android.content.Context.MODE_PRIVATE;

public class MapsFragment extends Fragment {

    UserDataBase userDataBase;
    String lat,log;
    FusedLocationProviderClient fusedLocationProviderClient;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {

            setUpDB();

            //show user location on map
            mapView(googleMap);

        }
    };

    private void mapView(GoogleMap googleMap) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login",MODE_PRIVATE);
        final DAO dao = userDataBase.dao();
        UserData userData = dao.login(sharedPreferences.getString("username",null),sharedPreferences.getString("password",null));
        if (!TextUtils.isEmpty(userData.getLat())){
            Double latitude = Double.valueOf(userData.getLat());
            Double longitude = Double.valueOf(userData.getLog());
            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(latLng).title(userData.getFirstName() + " " +userData.getLastName()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.setMinZoomPreference(8);
        }else {
            LatLng latLng = new LatLng(20.5937, 78.9629);
            googleMap.addMarker(new MarkerOptions().position(latLng).title("India"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    private void setUpDB() {
        userDataBase = Room.databaseBuilder(getContext(), UserDataBase.class,"userDB")
                .allowMainThreadQueries().build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);

        //Initialize fusedLocationProviderClient
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
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
            Toast.makeText(getContext(), "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("MissingPermission")
    private void getCurrentLocation() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                ||locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Location location = task.getResult();
                    if (location!=null){
                        lat = String.valueOf(location.getLatitude());
                        log = String.valueOf(location.getLongitude());
                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login",MODE_PRIVATE);
                        userDataBase.dao().mapLatLog(lat,log,sharedPreferences.getString("password",null));

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }
}