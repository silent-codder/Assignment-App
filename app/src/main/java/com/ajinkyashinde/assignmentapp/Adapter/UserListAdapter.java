package com.ajinkyashinde.assignmentapp.Adapter;

/**
 Developed BY: Ajinkya Shinde
 Designation: Android Learner
 Date: 07/08/2021
 **/

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.ajinkyashinde.assignmentapp.R;
import com.ajinkyashinde.assignmentapp.Room.UserData;
import com.ajinkyashinde.assignmentapp.Fragment.View_User_Map_Fragment;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.ViewHolder>{

    List<UserData> userData;

    public UserListAdapter(List<UserData> userData) {
        this.userData = userData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_list_view,parent,false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.mFullName.setText(userData.get(position).getFirstName() + " " +userData.get(position).getLastName());
        holder.mAddress.setText(userData.get(position).getAddress());
        holder.mBirthDate.setText(userData.get(position).getBirthDate());
        holder.mUserName.setText(userData.get(position).getUserName());
        Picasso.get().load(userData.get(position).getProfileUrl()).into(holder.mProfileImg);

        holder.btnViewMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragment = new View_User_Map_Fragment();
                Bundle bundle = new Bundle();
                bundle.putString("Lat",userData.get(position).getLat());
                bundle.putString("Log",userData.get(position).getLog());
                bundle.putString("FullName",userData.get(position).getFirstName() + " " +userData.get(position).getLastName());
                fragment.setArguments(bundle);
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).addToBackStack(null).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return userData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView mFullName,mUserName,mBirthDate,mAddress;
        CircleImageView mProfileImg;
        Button btnViewMap;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            mUserName = itemView.findViewById(R.id.userName);
            mFullName = itemView.findViewById(R.id.fullName);
            mBirthDate = itemView.findViewById(R.id.birthDate);
            mAddress = itemView.findViewById(R.id.address);
            mProfileImg = itemView.findViewById(R.id.userProfileImg);
            btnViewMap = itemView.findViewById(R.id.btnViewMap);
        }
    }
}
