package de.unipassau.android.bookshelf.Local;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import de.unipassau.android.bookshelf.model.BookDetails;


@Database(entities = BookDetails.class, version = DATABASE_VERSION)
public abstract class BooksDatabase extends RoomDatabase {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "EDMT-Database-Room";

        public abstract UserDAO userDAO();

        private static BooksDatabase mInstance;

        public static BooksDatabase getInstance(Context context){
            if(mInstance == null){
                mInstance = Room.databaseBuilder(context, BooksDatabase.class, DATABASE_NAME).fallbackToDestructiveMigration().build();

            }
        return mInstance;





}
