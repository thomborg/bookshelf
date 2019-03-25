package de.unipassau.android.bookshelf.model;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName = "BookPictures")
public class BookPicture {
    List <BookPicture> pictureList = new  <BookPicture>ArrayList();

    @ColumnInfo(name = "id")
    private int id;

    public BookPicture(int id){
        this.id = id;
    }

}
