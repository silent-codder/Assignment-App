package com.ajinkyashinde.assignmentapp.Fragment;
/**
 Developed BY: Ajinkya Shinde
 Designation: Android Learner
 Date: 06/07/2021
 **/

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ajinkyashinde.assignmentapp.Adapter.UserListAdapter;
import com.ajinkyashinde.assignmentapp.R;
import com.ajinkyashinde.assignmentapp.Room.UserData;
import com.ajinkyashinde.assignmentapp.Room.UserDataBase;

import java.util.List;

import static android.content.ContentValues.TAG;

public class ListFragment extends Fragment {

    UserDataBase userDataBase;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        setUpDB();
        List<UserData> data = userDataBase.dao().getUserData();

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerview);
        UserListAdapter adapter = new UserListAdapter(data);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        return view;
    }

    private void setUpDB(){
        userDataBase = Room.databaseBuilder(getContext(), UserDataBase.class,"userDB")
                .allowMainThreadQueries().build();
    }

}