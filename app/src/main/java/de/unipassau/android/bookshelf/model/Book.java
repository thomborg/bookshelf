package de.unipassau.android.bookshelf.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Michi
 */

@Entity
public class Book {
    @NonNull
    @PrimaryKey
    private String id;
    private String author;
    private String title;
    private String ISBN;
    private int numberOfPages;
    private String publishDate;
    private String urlThumbnail;
    // private List<BookPicture> bookPictureList;


    public Book(String author, String title, String ISBN, int numberOfPages, String publishDate, String urlThumbnail) {
        this.id = UUID.randomUUID().toString();
        this.author = author;
        this.title = title;
        this.ISBN = ISBN;
        this.numberOfPages = numberOfPages;
        this.publishDate = publishDate;
        this.urlThumbnail = urlThumbnail;

     //    bookPictureList = new ArrayList<>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }

    public int getNumberOfPages() {
        return numberOfPages;
    }

    public void setNumberOfPages(int numberOfPages) {
        this.numberOfPages = numberOfPages;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public String getUrlThumbnail() {
        return urlThumbnail;
    }

    public void setUrlThumbnail(String urlThumbnail) {
        this.urlThumbnail = urlThumbnail;
    }

    /*
    public List<BookPicture> getBookPictureList() {
        return bookPictureList;
    }

    public void setBookPictureList(List<BookPicture> bookPictureList) {
        this.bookPictureList = bookPictureList;
    }
    */
}
