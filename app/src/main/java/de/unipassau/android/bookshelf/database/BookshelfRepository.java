package de.unipassau.android.bookshelf.database;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;

import androidx.lifecycle.LiveData;
import de.unipassau.android.bookshelf.model.Book;

public class BookshelfRepository {
    private BookDAO bookDAO;
    private LiveData<List<Book>> bookList;

    public BookshelfRepository(Application application) {
        BookshelfDatabase db = BookshelfDatabase.getDatabase(application);
        bookDAO = db.daoAccess();
        bookList = bookDAO.fetchAllBooks();
    }

    public LiveData<List<Book>> getAllBooks() {
        return bookList;
    }

    public void insert (Book book) {
        new insertAsyncTask(bookDAO).execute(book);
    }

    private static class insertAsyncTask extends AsyncTask<Book, Void, Void> {

        private BookDAO mAsyncTaskDao;

        insertAsyncTask(BookDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Book... params) {
            mAsyncTaskDao.insertOnlySingleBook(params[0]);
            return null;
        }
    }

}


