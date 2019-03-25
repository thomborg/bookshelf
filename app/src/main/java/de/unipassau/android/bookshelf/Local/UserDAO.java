package de.unipassau.android.bookshelf.Local;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Query;

import java.util.List;

import de.unipassau.android.bookshelf.model.Book;
import de.unipassau.android.bookshelf.model.BookDetails;
import io.reactivex.Flowable;
@Dao
public interface UserDAO {
    @Query("SELECT * FROM books where autor = autor")
    Flowable<BookDetails>getBookByAutor(String autor);

    @Query("Select * from books")
    Flowable<List<BookDetails>>getAllBooks();

    void insertBooks (Book... books);

    void updateBooks (BookDetails... books);

    void deleteUser (BookDetails... books);

    @Query("Delete from books")
    void deleteAllBooks();




}
