package de.unipassau.android.bookshelf.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.graphics.Bitmap;

/**
 * Michi
 */
@Entity(tableName = "books")
public class BookDetails {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "autor")
    private String autor;

    @ColumnInfo(name = "titel")
    private String titel;

    @ColumnInfo(name = "ISBN")
    private String isbn;

    @ColumnInfo(name = "Number of Pages")
    private int numberOfPages;

    @ColumnInfo(name = "Publish Date")
    private String publishDate;

    @ColumnInfo(name = "Publish Place")
    private String publishPlace;

    @ColumnInfo(name = "Book Cover")
    private Bitmap bookCover;


    public BookDetails(String autor, String titel, String isbn, int numberOfPages, String publishDate, String publishPlace){
        this.autor = autor;
        this.titel = titel;
        this.isbn = isbn;
        this.numberOfPages = numberOfPages;
        this.publishDate = publishDate;
        this.publishPlace = publishPlace;

    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getAutor() {
        return autor;
    }

    public String getIsbn() {
        return isbn;
    }

    public String getTitel() {
        return titel;
    }


    @Override
    public String toString() {
        return new StringBuilder(autor).append("/n").append(titel).append("/n").append(isbn).toString();
    }

}
