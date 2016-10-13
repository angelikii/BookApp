
package com.example.android.BookApp;

import android.graphics.Bitmap;

import java.util.ArrayList;


public class Book {

    private String mTitle;
    private ArrayList<String> mAuthors;
    private Bitmap mThumbnail;

    public Book(String title, ArrayList<String> authors, Bitmap image) {
        mTitle = title;
        mAuthors = authors;
        mThumbnail = image;
    }

    public String getTitle() {
        return mTitle;
    }

    public ArrayList<String> getAuthors() {
        return mAuthors;
    }

    public Bitmap getImage() {
        return mThumbnail;
    }

}
