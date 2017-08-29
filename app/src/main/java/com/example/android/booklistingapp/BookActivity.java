package com.example.android.booklistingapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.widget.Toast.makeText;

public class BookActivity extends AppCompatActivity {

    /**
     * URL for Google Books Data set from the Google API
     */
    private static final String GOOGLE_BOOKS_REQUEST_URL =
            "https://www.googleapis.com/books/v1/volumes?maxResults=10&q=";

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_books);

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        // Create a new adapter that takes an empty list of books as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        //  Find and set Empty State Text View in activity_search_books.xml
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        // Find the Search Button in the activity_search_books.xml
        Button searchButton = (Button) findViewById(R.id.submit_query);

        // Set an OnClickListener on search button
        searchButton.setOnClickListener(new View.OnClickListener() {
            // This code will be executed when the search button is clicked on.
            @Override
            public void onClick(View view) {
                // Clear the adapter of previous book data
                mAdapter.clear();

                // Hide the virtual keyboard when the Search button is clicked on
                InputMethodManager inputManager = (InputMethodManager)
                        getSystemService(Context.INPUT_METHOD_SERVICE);

                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);

                // Get a reference to the ConnectivityManager to check state of network connectivity
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

                // Get details on the currently active default data network
                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                // boolean to check if there is a network connection
                final boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();

                // Check for internet connection
                // if the user is connected to the internet
                if (isConnected) {
                    // Find EditText View in activity_search_books.xml
                    EditText searchEditTextView = (EditText) findViewById(R.id.search_query);
                    // Get the text from the EditText View
                    String searchQuery = searchEditTextView.getText().toString();

                    // if EditText is empty
                    if (searchQuery.isEmpty()) {
                        // Show a Toast Message to inform the user
                        // that has to type something
                        Toast toast = makeText(getApplicationContext(), "Type something to Search", Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        // Hide Empty State View
                        mEmptyStateTextView.setVisibility(View.GONE);

                    } else {
                        // Show a Toast Message with the query of the user
                        Toast toast = Toast.makeText(getApplicationContext(), "Performing Search for: " + searchQuery, Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER | Gravity.CENTER_HORIZONTAL, 0, 0);
                        toast.show();

                        // Store the Google API url + the query of the user
                        String url = GOOGLE_BOOKS_REQUEST_URL + searchQuery;

                        // Start the AsyncTask to fetch the book data
                        BookAsyncTask task = new BookAsyncTask();
                        task.execute(url);
                    }
                } else {
                    // If there is no internet connection, display error
                    // First, hide loading indicator so error message will be visible
                    View loadingIndicator = findViewById(R.id.loading_indicator);
                    loadingIndicator.setVisibility(View.GONE);

                    // Update empty state with no internet connection error message
                    mEmptyStateTextView.setText(R.string.no_internet);
                }
            }
        });

        // Set an item click listener on the ListView, which sends an intent to a web browser
        // to open a website with more information about the selected book.
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current book that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the book URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the list of books in the response.
     * <p>
     * AsyncTask has three generic parameters: the input type, a type used for progress updates, and
     * an output type. Our task will take a String URL, and return a Book. We won't do
     * progress updates, so the second generic is just Void.
     */
    private class BookAsyncTask extends AsyncTask<String, Void, List<Book>> {
        // Find loading indicator
        View loadingIndicator = findViewById(R.id.loading_indicator);

        /**
         * This method runs on the UI thread before the task is executed.
         * This step is normally used to setup the task.
         */
        @Override
        protected void onPreExecute() {
            // Clear the adapter of previous book data
            mAdapter.clear();
            // Display loading indicator (VISIBLE)
            loadingIndicator.setVisibility(View.VISIBLE);
            // Hide Empty State View
            mEmptyStateTextView.setVisibility(View.GONE);
        }

        /**
         * This method runs on a background thread and performs the network request.
         * We should not update the UI from a background thread, so we return a list of
         * {@link Book}s as the result.
         */
        @Override
        protected List<Book> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            List<Book> result = QueryUtils.fetchBookData(urls[0]);
            return result;
        }

        /**
         * This method runs on the main UI thread after the background work has been
         * completed. This method receives as input, the return value from the doInBackground()
         * method. First we clear out the adapter, to get rid of book data from a previous
         * query to Google Books API. Then we update the adapter with the new list of books,
         * which will trigger the ListView to re-populate its list items.
         */
        @Override
        protected void onPostExecute(List<Book> data) {
            // Clear the adapter of previous book data
            mAdapter.clear();

            // If there is a valid list of {@link Book}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (data != null && !data.isEmpty()) {
                mAdapter.addAll(data);
                // Hide the loading indicator (GONE)
                loadingIndicator.setVisibility(View.GONE);
            } else {
                // Hide the loading indicator (GONE)
                loadingIndicator.setVisibility(View.GONE);
                // Set empty state text to display "No Books found."
                mEmptyStateTextView.setText(R.string.no_books);
            }
        }
    }
}
