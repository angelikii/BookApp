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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Helper methods related to requesting and receiving earthquake data from USGS.
 */
public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();


    /** This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    public static List<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, Resources.getSystem().getString(R.string.IOexceptionHTTP), e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Book}s
        List<Book> books = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Book}s
        return books;
    }

    /** Returns new URL object from the given string URL.*/
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, Resources.getSystem().getString(R.string.URLexception), e);
        }
        return url;
    }

    /** Make an HTTP request to the given URL and return a String as the response. */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, Resources.getSystem().getString(R.string.errorrespcode) + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, Resources.getSystem().getString(R.string.IOexceptionHTTP), e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /*Convert the {@link InputStream} into a String which contains the whole JSON response from the server.*/
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /* Return a list of {@link Book} objects that has been built up from parsing the given JSON response. */
    private static List<Book> extractFeatureFromJson(String bookJSON) {

        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        List<Book> books = new ArrayList<>();

        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(bookJSON);

            JSONArray bookArray = baseJsonResponse.getJSONArray("items");

            for (int i = 0; i < bookArray.length(); i++) {
                ArrayList<String> authors = new ArrayList<>();

                // Get a single book at position i within the list of books
                JSONObject currentBook = bookArray.getJSONObject(i);

                JSONObject volume = currentBook.getJSONObject("volumeInfo");

                String title = volume.getString("title");

                JSONArray authorArray = volume.getJSONArray("authors");
                for (int j = 0; j < authorArray.length(); j++) {
                    authors.add(authorArray.getString(j));
                }

                JSONObject imageLinks = volume.getJSONObject("imageLinks");
                String imageUrl = imageLinks.getString("thumbnail");

                //decoding the image
                URL imgUrl = new URL(imageUrl);
                Bitmap bmp = BitmapFactory.decodeStream(imgUrl.openConnection().getInputStream());

                Book book = new Book(title, authors, bmp);

                // Add the new {@link Book} to the list of books.
                books.add(book);
            }

        } catch (JSONException e) {
            Log.e(LOG_TAG, Resources.getSystem().getString(R.string.JSONexception), e);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, Resources.getSystem().getString(R.string.IMGexception), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, Resources.getSystem().getString(R.string.IOIMGexception), e);
        }

        return books;
    }

}
