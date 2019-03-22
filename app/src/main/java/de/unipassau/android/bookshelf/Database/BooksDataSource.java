package de.unipassau.android.bookshelf.Database;

import android.arch.persistence.room.Query;

import java.util.List;

import de.unipassau.android.bookshelf.Local.BooksDatabase;
import de.unipassau.android.bookshelf.model.BookDetails;
import io.reactivex.Flowable;

public interface BooksDataSource {
    @Query("SELECT * FROM books where autor = autor")
    Flowable<BookDetails>getBookByAutor(String autor);

    @Query("Select * from books")
    Flowable<List<BookDetails>>getAllBooks();

    void insertBooks (BookDetails... books);

    void updateBooks (BookDetails... books);

    void deleteUser (BookDetails... books);

    @Query("Delete from books")
    void deleteAllBooks();

}
