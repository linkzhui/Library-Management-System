package com.example.raymon.universitylibraryassistance;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by raymon on 12/4/17.
 */

public class User {

    String email;
    String universityID;
    int num_of_borrowed_book;
    public User(){

    }
    public User(String email, String universityID)
    {
        this.email = email;
        this.universityID = universityID;
        num_of_borrowed_book = 0;
    }
}
