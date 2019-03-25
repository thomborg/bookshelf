package de.unipassau.android.bookshelf.ui;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.model.BookPicture;
import de.unipassau.android.bookshelf.model.BookPictureAdapter;

public class PictureGalleryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.picturegallary);
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 3);
        recyclerView.setLayoutManager(layoutManager);


        ArrayList<BookPicture> createLists = new ArrayList<>();
        BookPictureAdapter adapter = new BookPictureAdapter(getApplicationContext(), createLists);
        recyclerView.setAdapter(adapter);


    }
}
