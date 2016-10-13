package com.example.android.BookApp;

import android.content.Context;
import android.content.AsyncTaskLoader;

import java.util.List;


public class BookLoader extends AsyncTaskLoader<List<Book>> {

    private String mUrl;

    public BookLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Book> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response, and extract a list of books.
        List<Book> books = QueryUtils.fetchBookData(mUrl);
        return books;
    }
}

