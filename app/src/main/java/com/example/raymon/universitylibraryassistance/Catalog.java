package com.example.raymon.universitylibraryassistance;

import java.util.Random;

/**
 * Created by raymon on 12/3/17.
 */


public class Catalog {
    String Author;
    String Title;
    String Call_number;
    String Publisher;
    String Year_of_publication;
    String Location_in_the_library;
    String Number_of_copies;
    String Current_status;
    String Key_words;
    String Coverage_image;


    Random rand = new Random();

    public Catalog(String author, String Call_number,String Publisher, String Year_publication,String Key_words, String Coverage_image)
    {
        //constructor with random location in the library and number of copies 1
        this.Author = author;
        this.Call_number = Call_number;
        this.Publisher = Publisher;
        this.Year_of_publication = Year_publication;
        this.Location_in_the_library =rand.nextInt(7)+1+"";
        this.Number_of_copies = 1+"";
        this.Current_status = "IDLE";
        this.Key_words=Key_words;
        this.Coverage_image = Coverage_image;
    }

    public Catalog(String author, String Call_number,String Publisher, String Year_publication, String Location_in_the_library, String Number_of_copies, String Key_words, String Coverage_image)
    {
        //constructor with random location in the library and number of copies 1
        this.Author = author;
        this.Call_number = Call_number;
        this.Publisher = Publisher;
        this.Year_of_publication = Year_publication;
        this.Location_in_the_library =rand.nextInt(7)+1+"";
        this.Number_of_copies = 1+"";
        this.Current_status = "IDLE";
        this.Key_words=Key_words;
        this.Coverage_image = Coverage_image;
        this.Number_of_copies = Number_of_copies;
        this.Location_in_the_library = Location_in_the_library;
    }

    public void setTitle(String Title)
    {
        this.Title = Title;
    }

    public void updateStatus()
    {
        if(Current_status == "IDLE")
        {
            Current_status = "LEAD OUT";
        }

        else{
            Current_status = "IDLE";
        }
    }

    public String getCurrent_status()
    {
        return Current_status;
    }

}
