/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.BookApp;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import static android.view.View.GONE;

public class BookActivity extends AppCompatActivity implements LoaderCallbacks<List<Book>> {

    private static final String GOOGLEBOOKS_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";
    String userInput;
    TextView mEmptyStateView;
    private BookAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_activity);

        // here we create and pass an adapter to our bookListView to inflate it
        ListView bookListView = (ListView) findViewById(R.id.list);
        mAdapter = new BookAdapter(this, new ArrayList<Book>());
        bookListView.setAdapter(mAdapter);

        // we locate the search button and the EditText view in the UI
        Button goButton = (Button) findViewById(R.id.search_button);
        final EditText inputSearch = (EditText) findViewById(R.id.inputSearch);

        // we call the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        //we locate the empty state view
        mEmptyStateView = (TextView) findViewById(R.id.empty_state);

        //we create and initialize a Loader Manager
        final LoaderManager loaderManager = getLoaderManager();
        loaderManager.initLoader(0, null, BookActivity.this);

        // when search button is pressed, the entry of the user is saved and if the network is ok....
        goButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput
                userInput = inputSearch.getText().toString().replace(" ", "+");

                if (networkInfo != null && networkInfo.isConnected()) {
                    getLoaderManager().restartLoader(0, null, BookActivity.this);

                } else {
                    // we update empty state with no connection error message
                    mEmptyStateView.setText(getString(R.string.NetworkError));
                }

            }
        });
    }


    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle bundle) {
        //we create and load the loader with a valid url
        Log.v("BookActivity", "our url is " + GOOGLEBOOKS_URL + userInput);
        return new BookLoader(this, GOOGLEBOOKS_URL + userInput);

    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {

        mAdapter.clear();

        if (books != null && !books.isEmpty()) {
            //in case the previous search resulted in an empty state (no books)
            mEmptyStateView.setVisibility(GONE);
            //inflate list with adapter
            mAdapter.addAll(books);
        } else {
            mEmptyStateView.setVisibility(View.VISIBLE);
            mEmptyStateView.setText(getString(R.string.emptystate));
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        mAdapter.clear();
    }

}
