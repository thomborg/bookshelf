package de.unipassau.android.bookshelf.Local;

import java.util.List;

import de.unipassau.android.bookshelf.Database.BooksDataSource;
import de.unipassau.android.bookshelf.model.BookDetails;
import io.reactivex.Flowable;

public class IBooksDataSource implements BooksDataSource {

    private UserDAO userDAO;
    private static IBooksDataSource mInstance;

    public IBooksDataSource(UserDAO userDAO){

        this.userDAO = userDAO;
    }
    public static IBooksDataSource getInstance (UserDAO userDAO){
        if(mInstance == null){
            mInstance = new IBooksDataSource(userDAO);

        }
        return mInstance;
    }

    public Flowable<BookDetails> getBookByAutor(String autor) {
        return userDAO.getBookByAutor(autor);
    }


    @Override
    public Flowable<List<BookDetails>> getAllBooks() {

        return userDAO.getAllBooks();
    }

    @Override
    public void insertBooks(BookDetails... books) {
            userDAO.insertBooks(books);
    }

    @Override
    public void updateBooks(BookDetails... books) {
            userDAO.updateBooks(books);
    }

    @Override
    public void deleteUser(BookDetails... books) {
            userDAO.deleteUser(books);
    }

    @Override
    public void deleteAllBooks() {
            userDAO.deleteAllBooks();

    }
}
