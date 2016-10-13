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

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class BookAdapter extends ArrayAdapter<Book> {


    public BookAdapter(Context context, List<Book> books) {
        super(context, 0, books);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.book_list_item, parent, false);
        }

        Book currentBook = getItem(position);

        TextView titleView = (TextView) listItemView.findViewById(R.id.title);
        String bookTitle = currentBook.getTitle();
        titleView.setText(bookTitle);

        TextView authorsView = (TextView) listItemView.findViewById(R.id.authors_list);
        ArrayList<String> bookAuthors = currentBook.getAuthors();
        StringBuilder authorsStrB = new StringBuilder();
        //make a string with commas from the list of authors
        for (int i = 0; i < bookAuthors.size(); i++) {
            authorsStrB.append(bookAuthors.get(i));
            if (bookAuthors.size() > 0 && i < bookAuthors.size() - 1) {
                authorsStrB.append(", ");
            }
        }
        authorsView.setText(authorsStrB.toString());

        //set the image
        ImageView iv = (ImageView) listItemView.findViewById(R.id.book_thumbnail);
        Bitmap thumbnail = currentBook.getImage();
        iv.setImageBitmap(thumbnail);


        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }

}
