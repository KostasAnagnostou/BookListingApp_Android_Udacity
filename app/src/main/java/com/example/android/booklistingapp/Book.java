package com.example.android.booklistingapp;

/**
 * Created by Kostas on 13/7/2017.
 */

public class Book {

    /*
     * The title of the Book
     */
    private String mTitle;

    /*
     * The Author of the Book
     */
    private String mAuthor;

    /*
     *  The Publish Date of the Book
     */
    private String mDate;

    // Url for the Information (infoLink key) of the Book from Google Books API
    private String mUrl;

    /*
     *  Set the Constructor
     *  @param
     *  @param
     */
    public Book(String title, String author, String date, String url) {
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mUrl = url;
    }

    // Get (returns) the Title of the Book
    public String getTitle() {
        return mTitle;
    }

    // Get (returns) the Author of the Book
    public String getAuthor() {
        return mAuthor;
    }

    // Get (returns) the Date of the Book
    public String getDate() {
        return mDate;
    }

    // Get (returns) the infoLink key with the url of the Book
    public String getUrl() {
        return mUrl;
    }
}
