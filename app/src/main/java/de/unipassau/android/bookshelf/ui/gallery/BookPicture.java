package de.unipassau.android.bookshelf.ui.gallery;

import android.graphics.Bitmap;

import java.io.File;

public class BookPicture {

    private String path;
    private Bitmap imageBitmap;

    public BookPicture(String path, Bitmap picture) {
        this.path = path;
        imageBitmap = picture;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap imageBitmap) {
        this.imageBitmap = imageBitmap;
    }

    public boolean delete() {
        File file = new File(path);
        return file.delete();
    }

}
