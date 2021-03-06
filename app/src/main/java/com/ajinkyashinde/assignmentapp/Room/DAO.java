package com.ajinkyashinde.assignmentapp.Room;

/**
 Developed BY: Ajinkya Shinde
 Designation: Android Learner
 Date: 07/08/2021
 **/

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DAO {

    @Insert
    public void userInfo(UserData userData);

    @Query("Select * From UserData")
    List<UserData> getUserData();

    @Query("Select * From UserData where userName=(:userName) and password=(:password)")
    UserData login(String userName,String password);


    @Query("UPDATE UserData set firstName = :firstName, lastName = :lastName, userName = :userName, birthDate = :birthDate, address = :address where password =(:password)" )
    void update(String firstName,String lastName,String userName,String birthDate,String address,String password);

    @Query("UPDATE UserData set ProfileUrl = :profileUrl where password =(:password)")
    void updateProfile(String profileUrl,String password);

    @Query("UPDATE UserData set lat = :lat, log = :log where password =(:password)")
    void mapLatLog(String lat,String log,String password);

}
