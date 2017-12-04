package com.example.raymon.universitylibraryassistance;

/**
 * Created by raymon on 12/3/17.
 */

public class User {
    public String email;
    public String universityID;
    public  User(){

    }

    public User(String email, String universityID)
    {
        this.email = email;
        this.universityID = universityID;
    }
}
