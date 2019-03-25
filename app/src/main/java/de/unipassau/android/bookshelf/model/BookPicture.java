package de.unipassau.android.bookshelf.model;

import android.graphics.Bitmap;

public class BookPicture {
    private Bitmap imageBitmap;

    public BookPicture(Bitmap picture) {
        this.imageBitmap = picture;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
}
