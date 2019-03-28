package de.unipassau.android.bookshelf.model;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;

/**
 * Michi
 */
public class Book {
    LocationManager locationManger = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    Location lastLocation =
            locationManger.getLastKnownLocation(locationManager.GPS_PROVIDER);

    private String author;
    private String title;
    private String ISBN;
    private int numberOfPages;
    private String publishDate;
    private String publishPlaces = BookLocation.address;

    publishPlaces = Context.LOCATION_SERVICE;
    LocationManager locationManger = (LocationManager)
            getSystemService(service);
    String provider = LocationManger.GPS_PROVIDER;
    @SuppressLint("MissingPermission")
    Location location =
            locationManger.getLastKnownLocation(provider);

    public Book(String author, String title, String ISBN, int numberOfPages, String publishDate, String publishPlaces) {
        this.author = author;
        this.title = title;
        this.ISBN = ISBN;
        this.numberOfPages = numberOfPages;
        this.publishDate = publishDate;
        this.publishPlaces = publishPlaces;

    }



    public Book(Book book) {

    }

    public void bookExample(){
        Book book = new Book("Lehmann", "Der Wald", "298347", 234, "2001", "Ohio");
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

    public String getPublishPlaces() {
        return publishPlaces;
    }

    public void setPublishPlaces(String publishPlaces) {
        this.publishPlaces = publishPlaces;
    }
}
