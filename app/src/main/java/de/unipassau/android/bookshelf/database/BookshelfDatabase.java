package de.unipassau.android.bookshelf.database;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;
import de.unipassau.android.bookshelf.model.Book;

/**
 * Room-Datenbank der App.
 */
@Database(entities = {Book.class}, version = 1, exportSchema = false)
public abstract class BookshelfDatabase extends RoomDatabase {
    public abstract BookDAO daoAccess();

    private static final String DATABASE_NAME = "bookshelf-db";

    private static volatile BookshelfDatabase INSTANCE;

    public static BookshelfDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BookshelfDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            BookshelfDatabase.class, DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();

                }
            }
        }
        return INSTANCE;
    }


    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final BookDAO mDao;

        PopulateDbAsync(BookshelfDatabase db) {
            mDao = db.daoAccess();
        }

        @Override
        protected Void doInBackground(final Void... params) {
            return null;
        }
    }

}


