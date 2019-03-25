package de.unipassau.android.bookshelf.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Michi
 */
public class BookAdapter extends ArrayAdapter<Book> {
    private List<Book> bookList;
    private Context context;
    private MyDatabaseHelper helper;
    private SQLiteDatabase db;

    public BookAdapter(Context context, List<Book> booksList){
        super(context, 0, booksList);
        this.bookList = booksList;
        this.context = context;
        helper = new MyDatabaseHelper(context, DB_NAME, null, DB_VERSION);
    }
    // Datenbanksetup
    public static final String DB_NAME = "Bookshelf";
    public static final int DB_VERSION = 1;

    // Relationenmodell
    // 3 Spalten in der Tabelle my-example-table
    // _id, first-example, second-example
    public static final String TABLE_NAME = "Bookstore";
    public static final String AUTHOR_ID = "Autor";
    public static final String NAME = "Name";
    public static final String ISBN = "ISBN";



    // Öffnen der Datenbankverbindung
    public void open() {
        db = helper.getWritableDatabase();
    }

    // Schließen der Datenbankverbindung
    public void close() {
        db.close();
        helper.close();
    }


    // Beispielmethode: Objekt in my-example-table einfügen
    public long insertMyObject(Object myExampleObject) {
        // Datensammlung für den einzufügenden Datensatz erstellen (ContentValues)
        // nutzt Schlüssel-Wert-Mechanismus
        // es werden die Konstanten v. o. genutzt, um Fehler zu vermeiden
        ContentValues v = new ContentValues();
        v.put(NAME, myExampleObject.toString());
        v.put(ISBN, myExampleObject.toString()); // exemparisch einfach toString()
        long newInsertId = db.insert(TABLE_NAME, null, v);
        return newInsertId;
    }

    // Beispielmethode: alle Einträge aus my-example-table holen
    public Cursor getAllMyObjects() {
        String[] allColumns = new String[] { AUTHOR_ID, NAME, ISBN };
        Cursor results = db.query(TABLE_NAME, allColumns, null, null, null, null, null);
        return results;
    }


    // Beispielmethode: Ein myObject-Tupel löschen
    public void removeMyObject(long id) {
        String toDelete = AUTHOR_ID + "=?";
        String[] deleteArgs = new String[] { String.valueOf(id) };
        db.delete(TABLE_NAME, toDelete, deleteArgs);
    }




    // Interne Ableitung der Hilfsklasse SQLiteOpenHelper zur Erstellung der Tabellen
    private class MyDatabaseHelper extends SQLiteOpenHelper {

        // Hier wird über das SQL Statement das Datenmodell festgelegt
        private static final String CREATE_DB = "create table " + TABLE_NAME + " (" + AUTHOR_ID + " integer primary key autoincrement, " + NAME + " text not null, " + ISBN + " text not null);";

        public MyDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(CREATE_DB);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Upgrage bei Versionsänderung: Wie hat sich das Datenmodell verändert? Immer individuell je nach Datenbankversion!
        }
    }
}



