package de.unipassau.android.bookshelf.database;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import de.unipassau.android.bookshelf.model.Book;

@Dao
public interface BookDAO {
    @Insert
    void insertOnlySingleBook (Book book);

    @Insert
    void insertMultipleMovies (List<Book> bookList);

    @Query("SELECT * FROM Book")
    LiveData<List<Book>> fetchAllBooks();

    @Query("SELECT * FROM Book WHERE id = :bookId")
    Book fetchOneBookById (int bookId);
    @Update
    void updateBook (Book book);
    @Delete
    void deleteBook (Book book);

    @Query("DELETE FROM Book")
    void deleteAllBooks ();
}

