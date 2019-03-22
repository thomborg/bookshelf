package de.unipassau.android.bookshelf.Database;

import java.util.List;

import de.unipassau.android.bookshelf.Local.IBooksDataSource;
import de.unipassau.android.bookshelf.model.BookDetails;
import io.reactivex.Flowable;

public class BooksRepository implements BooksDataSource {

    private IBooksDataSource mLocalDataSource;
    private static BooksRepository mInstance;

    public BooksRepository(IBooksDataSource mLocalDataSource){
        this.mLocalDataSource = mLocalDataSource;


    }
    public static BooksRepository getInstance(IBooksDataSource mLocalDataSource){
        if(mInstance==null) {
            mInstance = new BooksRepository(mLocalDataSource);
        }
        return mInstance;
    }

    public Flowable<BookDetails> getBookByAutor(String autor) {
        return mLocalDataSource.getBookByAutor(autor);
    }


    @Override
    public Flowable<List<BookDetails>> getAllBooks() {

        return mLocalDataSource.getAllBooks();
    }

    @Override
    public void insertBooks(BookDetails... books) {
        mLocalDataSource.insertBooks(books);
    }

    @Override
    public void updateBooks(BookDetails... books) {
        mLocalDataSource.updateBooks(books);
    }

    @Override
    public void deleteUser(BookDetails... books) {
        mLocalDataSource.deleteUser(books);
    }

    @Override
    public void deleteAllBooks() {
        mLocalDataSource.deleteAllBooks();

    }
}
