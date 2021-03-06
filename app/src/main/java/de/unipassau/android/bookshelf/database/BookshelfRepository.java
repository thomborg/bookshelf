package de.unipassau.android.bookshelf.database;

import android.app.Application;
import android.os.AsyncTask;

import java.util.List;
import java.util.concurrent.ExecutionException;

import androidx.lifecycle.LiveData;
import de.unipassau.android.bookshelf.model.Book;

/**
 * Repository von der Datenbank.
 */
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

    public void insert(Book book) {
        new insertAsyncTask(bookDAO).execute(book);
    }

    public void delete(Book book) {
        new deleteAsyncTask(bookDAO).execute(book);
    }

    public Book findBookById(String id) {
        try {
            return new findBookByIdAsyncTask(bookDAO).execute(id).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setShelfofBook(String bookId, String shelf) {
        new setShelfofBookAsyncTask(bookDAO).execute(bookId, shelf);
    }

    public void setLocationofBookAsyncTask(String bookId, String location) {
        new setLocationofBookAsyncTask(bookDAO).execute(bookId, location);
    }

    public String[] getAllShelfs() {
        try {
            return new getAllShelfsAsyncTask(bookDAO).execute().get();
        } catch (ExecutionException e) {
            e.printStackTrace();
            return null;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return null;
        }
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

    private static class deleteAsyncTask extends AsyncTask<Book, Void, Void> {

        private BookDAO mAsyncTaskDao;

        deleteAsyncTask(BookDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Book... params) {
            mAsyncTaskDao.deleteBook(params[0]);
            return null;
        }
    }

    private static class findBookByIdAsyncTask extends AsyncTask<String, Void, Book> {

        private BookDAO mAsyncTaskDao;

        findBookByIdAsyncTask(BookDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Book doInBackground(final String... params) {
            return mAsyncTaskDao.fetchOneBookById(params[0]);
        }
    }

    private static class setShelfofBookAsyncTask extends AsyncTask<String, Void, Void> {

        private BookDAO mAsyncTaskDao;

        setShelfofBookAsyncTask(BookDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            mAsyncTaskDao.setShelfofBook(params[0], params[1]);
            return null;
        }
    }

    private static class setLocationofBookAsyncTask extends AsyncTask<String, Void, Void> {

        private BookDAO mAsyncTaskDao;

        setLocationofBookAsyncTask(BookDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            mAsyncTaskDao.setLocationOfBook(params[0], params[1]);
            return null;
        }
    }


    private static class getAllShelfsAsyncTask extends AsyncTask<Void, Void, String[]> {

        private BookDAO mAsyncTaskDao;

        getAllShelfsAsyncTask(BookDAO dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected String[] doInBackground(Void... voids) {
            return mAsyncTaskDao.selectAllShelfs();
        }
    }
}


