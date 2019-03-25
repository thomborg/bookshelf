package de.unipassau.android.bookshelf.model;

import android.graphics.Bitmap;

import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class BookPicture {
    @NonNull
    @PrimaryKey
    private String id;
    private Bitmap imageBitmap;

    public BookPicture(Bitmap picture) {
        id = UUID.randomUUID().toString();
        this.imageBitmap = picture;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }
}
