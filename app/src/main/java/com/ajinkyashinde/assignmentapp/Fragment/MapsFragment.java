package com.ajinkyashinde.assignmentapp.Fragment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajinkyashinde.assignmentapp.R;
import com.ajinkyashinde.assignmentapp.Room.DAO;
import com.ajinkyashinde.assignmentapp.Room.UserData;
import com.ajinkyashinde.assignmentapp.Room.UserDataBase;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.content.Context.MODE_PRIVATE;

public class MapsFragment extends Fragment {

    UserDataBase userDataBase;


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
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("Login",MODE_PRIVATE);
            final DAO dao = userDataBase.dao();
            UserData userData = dao.login(sharedPreferences.getString("username",null),sharedPreferences.getString("password",null));
            Double latitude = Double.valueOf(userData.getLat());
            Double longitude = Double.valueOf(userData.getLog());
            LatLng latLng = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(latLng).title(userData.getFirstName() + " " +userData.getLastName()));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    };

    private void setUpDB() {
        userDataBase = Room.databaseBuilder(getContext(), UserDataBase.class,"userDB")
                .allowMainThreadQueries().build();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_maps, container, false);
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