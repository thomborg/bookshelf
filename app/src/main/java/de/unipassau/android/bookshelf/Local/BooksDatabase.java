package de.unipassau.android.bookshelf.Local;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import de.unipassau.android.bookshelf.model.Book;
import de.unipassau.android.bookshelf.model.BookDetails;


@Database(entities = {BookDetails.class}, version = 1)
public abstract class BooksDatabase extends RoomDatabase {
    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "EDMT-Database-Room";

    public abstract UserDAO userDAO();

    private static BooksDatabase mInstance;

    public static synchronized BooksDatabase getInstance(Context context) {
        if (mInstance == null) {
            mInstance = Room.databaseBuilder(context, BooksDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

        }
        return mInstance;


    }

    private static RoomDatabase.Callback booksdatabase = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {


            super.onCreate(db);
            new populateDbAsyncTask(mInstance).execute();
        }
    };

    public BooksDatabase getInstance() {
        return null;
    }

    private static class populateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        UserDAO userDAO;

        populateDbAsyncTask(BooksDatabase db) {
            userDAO = db.userDAO();

        }

        protected Void doInBackground(Void... voids) {
            userDAO.insertBooks(new Book("Lehner", "Wissenmanagement", "342423", 344, "2010", "Ohio"));
            userDAO.insertBooks(new Book("Dszepina", "Datenmanagement", "283742", 400, "2010", "Ohio"));
            userDAO.insertBooks(new Book("Fteimi", "Winfo", "92378492", 437, "2010", "Ohio"));
            return null;
        }
    }
}