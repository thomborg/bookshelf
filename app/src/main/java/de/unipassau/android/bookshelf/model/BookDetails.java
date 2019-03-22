package de.unipassau.android.bookshelf.model;


import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

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

    public BookDetails(){

    }
    public BookDetails(String autor, String titel, String isbn){
        this.autor = autor;
        this.titel = titel;
        this.isbn = isbn;

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
