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
    String isbn_thirten;
    String isbn_ten;
    boolean idle;

    public String getAuthor() {
        return author;
    }



    public Catalog(String Author, String Call_number, String Publisher, String Year_of_publication, String Keywords, String Coverage_image, String Current_status, String ISBN_13, String ISBN_10) {
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
        this.isbn_ten = ISBN_10;
        this.isbn_thirten = ISBN_13;
    }

    public Catalog(String author, String title, String call_number, String publisher, String year_of_publication, int location_in_the_library, int number_of_copies, String current_status, String keywords, String coverage_image, String isbn_thirten, String isbn_ten, boolean idle) {
        this.author = author;
        this.title = title;
        this.call_number = call_number;
        this.publisher = publisher;
        this.year_of_publication = year_of_publication;
        this.location_in_the_library = location_in_the_library;
        this.number_of_copies = number_of_copies;
        this.current_status = current_status;
        this.keywords = keywords;
        this.coverage_image = coverage_image;
        this.isbn_thirten = isbn_thirten;
        this.isbn_ten = isbn_ten;
        this.idle = idle;
    }

    public Catalog(String Author, String Call_number, String Publisher, String Year_of_publication, String Keywords, String Coverage_image, int Number_of_copies, String Current_status, int Location_in_the_library) {
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

    public Catalog() {

    }

    public void changeToBorrow() {
        current_status = "Borrow";
    }

    public void changeToIDLE() {
        current_status = "IDLE";
    }

    public void setTitle(String Title) {

        title = Title;
    }

    private int randfloor() {
        Random rand = new Random();
        return rand.nextInt(7) + 1;
    }


    public void setAuthor(String author) {
        this.author = author;
    }

    public void setCall_number(String call_number) {
        this.call_number = call_number;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public void setYear_of_publication(String year_of_publication) {
        this.year_of_publication = year_of_publication;
    }

    public void setLocation_in_the_library(int location_in_the_library) {
        this.location_in_the_library = location_in_the_library;
    }

    public void setNumber_of_copies(int number_of_copies) {
        this.number_of_copies = number_of_copies;
    }

    public void setCurrent_status(String current_status) {
        this.current_status = current_status;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public void setCoverage_image(String coverage_image) {
        this.coverage_image = coverage_image;
    }

    public void setIsbn_thirten(String ISBN13) {
        this.isbn_thirten = ISBN13;
    }

    public void setIsbn_ten(String ISBN10)
    {
        this.isbn_ten = ISBN10;
    }

    public void setIdle(boolean idle) {
        this.idle = idle;
    }

    public String getTitle() {
        return title;
    }

    public String getCall_number() {
        return call_number;
    }

    public String getPublisher() {
        return publisher;
    }

    public String getYear_of_publication() {
        return year_of_publication;
    }

    public int getLocation_in_the_library() {
        return location_in_the_library;
    }

    public int getNumber_of_copies() {
        return number_of_copies;
    }

    public String getCurrent_status() {
        return current_status;
    }

    public String getKeywords() {
        return keywords;
    }

    public String getCoverage_image() {
        return coverage_image;
    }

    public String getIsbn_thirten() {
        return isbn_thirten;
    }

    public String getIsbn_ten() {
        return isbn_ten;
    }

    public boolean getIdle() {
        return idle;
    }
}
