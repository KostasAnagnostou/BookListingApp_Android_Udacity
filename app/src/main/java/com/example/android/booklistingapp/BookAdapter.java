package com.example.android.booklistingapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Kostas on 13/7/2017.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * Constructs a new {@link BookAdapter} object.
     *
     * @param context is the current context (i.e. Activity) that the adapter is being created in.
     * @param books   is the list of {@link Book}s to be displayed.
     */
    public BookAdapter(Activity context, ArrayList<Book> books) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        super(context, 0, books);
    }

    /*
   * Provides a view for an AdapterView (ListView, GridView, etc.)
    *  Returns a list item view that displays information about the Book
    *  at the given position in the list of Books
    */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        // Find the Book at the given position in the list of Books
        Book currentBook = getItem(position);

        // Find the TextView with view ID author_name
        TextView authorNameView = (TextView) listItemView.findViewById(R.id.author_name);
        // Display the author name of the current book in that TextView
        authorNameView.setText(currentBook.getAuthor());

        // Find the TextView with view ID title_book
        TextView titleBookView = (TextView) listItemView.findViewById(R.id.title_book);
        // Display the title of the current book in that TextView
        titleBookView.setText(currentBook.getTitle());

        // Find the TextView with view ID publish_date
        TextView publishDateView = (TextView) listItemView.findViewById(R.id.publish_date);
        // Display the publish date of the current book in that TextView
        publishDateView.setText(currentBook.getDate());

        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }
}