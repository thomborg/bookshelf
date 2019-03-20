package de.unipassau.android.bookshelf.model;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Michi
 */
public class BookAdapter extends ArrayAdapter<Book> {
    private List<Book> bookList;
    private Context context;

    public BookAdapter(Context context, List<Book> booksList){
        super(context, 0, booksList);
        this.bookList = booksList;
        this.context = context;
    }


}
