package de.unipassau.android.bookshelf.model;

import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import java.io.File;


/**
 * Klasse für ein BookPicture. Generiert bei Erstellung ein Thumbnail des Bildes.
 */
public class BookPicture {
    private static int THUMBNAIL_HEIGHT = 240;
    private static int THUMBNAIL_WIDTH = 120;

    private String path;
    private Bitmap imageThumbnail;

    public BookPicture(String path, Bitmap picture) {
        this.path = path;
        imageThumbnail = ThumbnailUtils.extractThumbnail(picture, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }


    public Bitmap getImageThumbnail() {
        return imageThumbnail;
    }

    public boolean delete() {
        File file = new File(path);
        return file.delete();
    }

}
