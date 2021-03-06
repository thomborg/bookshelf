package de.unipassau.android.bookshelf.model;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import de.unipassau.android.bookshelf.database.BookshelfRepository;

/**
 * View Model für die Book-Objekte.
 */
public class BookViewModel extends AndroidViewModel {
    private BookshelfRepository mRepository;

    private LiveData<List<Book>> mAllBooks;

    public BookViewModel(@NonNull Application application) {
        super(application);
        this.mRepository = new BookshelfRepository(application);
        this.mAllBooks = mRepository.getAllBooks();
    }

    public LiveData<List<Book>> getAllBooks() {
        return mAllBooks;
    }

    public void insert(Book book) {
        mRepository.insert(book);
    }

    public void delete(Book book) {
        mRepository.delete(book);
    }

    public Book findBookById(String id) {
        return mRepository.findBookById(id);
    }

    public String[] getAllShelfs() {
        return mRepository.getAllShelfs();
    }

    public void setShelf(String bookId, String shelf) {
        mRepository.setShelfofBook(bookId, shelf);
    }

    public void setLocation(String bookId, String location) {
        mRepository.setLocationofBookAsyncTask(bookId, location);
    }
}
