package de.unipassau.android.bookshelf.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import de.unipassau.android.bookshelf.MainActivity;
import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.model.BookPicture;
import de.unipassau.android.bookshelf.network.ResultDTO;
import de.unipassau.android.bookshelf.ui.barcodereader.BarcodeScanActivity;

/**
 * Artur
 */
public class DisplayBookActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ArrayList<BookPicture> sampleArrayListNoProductionPls;


    TextView title, subtitle, author, isbn, publishedDate, nrPages, nrPictures;
    ImageView cover;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaybook);

        sampleArrayListNoProductionPls = new ArrayList<BookPicture>();


        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String bookId = bundle.getString("id");
        }

        setTitle("Artur");

        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        author = findViewById(R.id.author);
        isbn = findViewById(R.id.isbn);
        publishedDate = findViewById(R.id.publishedDate);
        nrPages = findViewById(R.id.nrPages);
        cover = findViewById(R.id.imageView);
        nrPictures = findViewById(R.id.nrOfPictures);

        title.setText("Hallo");
        subtitle.setText("Tschüss");
        author.setText("Artur");
        isbn.setText("1234567890");
        publishedDate.setText("23.03.2019");
        nrPages.setText("100");
        nrPictures.setText(String.valueOf(sampleArrayListNoProductionPls.size()));

        FloatingActionButton cameraButton = (FloatingActionButton) findViewById(R.id.take_picture);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent();
            }
        });

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            cover.setImageBitmap(imageBitmap);
            BookPicture bookPicture = new BookPicture(imageBitmap);
            sampleArrayListNoProductionPls.add(bookPicture);

        }
    }


}
