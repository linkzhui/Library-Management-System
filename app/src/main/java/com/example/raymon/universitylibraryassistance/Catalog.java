package com.example.raymon.universitylibraryassistance;

import android.graphics.Bitmap;

import java.util.List;
import java.util.Random;

/**
 * Created by raymon on 12/4/17.
 */

public class Catalog {
    String author;
    String title;
    String call_number;
    String publisher;
    String year_of_publication;
    int location_in_the_library;
    int number_of_copies;
    String current_status;
    String keywords;
    String coverage_image;

    public Catalog(String Author, String Call_number, String Publisher,String Year_of_publication,String Keywords,String Coverage_image,String Current_status){
        //constructor no number_of_copies, current_status, location_in_the_library
        author = Author;
        call_number = Call_number;
        publisher = Publisher;
        year_of_publication = Year_of_publication;
        keywords = Keywords;
        coverage_image = Coverage_image;
        location_in_the_library = randfloor();
        number_of_copies = 1;
        current_status = Current_status;
    }

    public Catalog(String Author, String Call_number, String Publisher,String Year_of_publication,String Keywords,String Coverage_image, int Number_of_copies, String Current_status, int Location_in_the_library)
    {
        author = Author;
        call_number = Call_number;
        publisher = Publisher;
        year_of_publication = Year_of_publication;
        keywords = Keywords;
        coverage_image = Coverage_image;
        location_in_the_library = Location_in_the_library;
        number_of_copies = Number_of_copies;
        current_status = Current_status;
    }
    public void changeToBorrow()
    {
        current_status = "Borrow";
    }

    public void changeToIDLE()
    {
        current_status = "IDLE";
    }

    public void setTitle(String Title)
    {
        title = Title;
    }

    private int randfloor()
    {
        Random rand = new Random();
        return rand.nextInt(7)+1;
    }

    public boolean isIDLE()
    {
        return current_status == "IDLE";
    }
}
