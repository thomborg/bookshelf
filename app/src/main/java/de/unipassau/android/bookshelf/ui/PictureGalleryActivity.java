package de.unipassau.android.bookshelf.ui;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.ui.gallery.BookPicture;
import de.unipassau.android.bookshelf.model.BookPictureAdapter;

public class PictureGalleryActivity extends AppCompatActivity {
        static List<BookPicture>bookPictureList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.picturegallary);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        String bookId = "test";


        bookPictureList = new ArrayList<>();
        loadBookPicture(bookId);

        BookPictureAdapter bookPictureAdapter = new BookPictureAdapter(getApplicationContext(), bookPictureList);




    }


    private void loadBookPicture(String bookId) {
        List<BookPicture> bookPictures = new ArrayList<>();
        String dirPath = bookId;

        File dataDir = new File(android.os.Environment.getDataDirectory(), dirPath);
        if (dataDir.isDirectory()) {
            File[] fileList = dataDir.listFiles();

            for (File file : fileList) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bookPictureList.add(new BookPicture(file.getPath(), imageBitmap));
            }
        }


    }


}
