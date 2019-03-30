package de.unipassau.android.bookshelf.ui.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.model.BookPicture;
import de.unipassau.android.bookshelf.model.BookPictureAdapter;
import de.unipassau.android.bookshelf.ui.DisplayBookActivity;

/**
 * Activity um gespeicherte Bilder als Gallerie anzuzeigen. Durch klicken öffentet sich das ausgwählte Bild in Fullscreenn und durch langes Tippen kann es gelöscht werden.
 */
public class PictureGalleryActivity extends AppCompatActivity {
    private String storagePath;
    private List<BookPicture> bookPictureList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_gallery);

        Intent intent = getIntent();

        storagePath = intent.getStringExtra(DisplayBookActivity.PICTURE_STORAGE_PATH);


        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.picturegallery);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);

        bookPictureList = new ArrayList<>();
        loadBookPictures();
        BookPictureAdapter bookPictureAdapter = new BookPictureAdapter(bookPictureList);
        recyclerView.setAdapter(bookPictureAdapter);

    }


    private void loadBookPictures() {

        File dataDir = new File(storagePath);
        if (dataDir.isDirectory()) {
            File[] fileList = dataDir.listFiles();

            for (final File file : fileList) {
                Bitmap imageBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                bookPictureList.add(new BookPicture(file.getPath(), imageBitmap));
            }
        }


    }


}
