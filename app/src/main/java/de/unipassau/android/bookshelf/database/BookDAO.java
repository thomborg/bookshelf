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
    void insertMultipleBooks (List<Book> bookList);

    @Query("SELECT * FROM Book")
    LiveData<List<Book>> fetchAllBooks();

    @Query("SELECT * FROM Book WHERE id = :bookId")
    Book fetchOneBookById (String bookId);

    @Query("SELECT * FROM Book WHERE shelf = :shelf")
    LiveData<List<Book>> getBooksWithShelf (String shelf);

    @Update
    void updateBook (Book book);

    @Delete
    void deleteBook (Book book);

    @Query("DELETE FROM Book")
    void deleteAllBooks ();

    @Query("UPDATE Book SET shelf = :shelf WHERE id = :bookId")
    void setShelfofBook(String bookId, String shelf);

    @Query("UPDATE Book SET location = :location WHERE id = :bookId")
    void setLocationOfBook(String bookId, String location);

    @Query("SELECT DISTINCT shelf FROM Book WHERE shelf IS NOT NULL")
    String[] selectAllShelfs();
}

