package de.unipassau.android.bookshelf.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.core.app.ActivityCompat;
import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.model.Book;
import de.unipassau.android.bookshelf.model.BookViewModel;
import de.unipassau.android.bookshelf.ui.gallery.BookPictureStorage;
import de.unipassau.android.bookshelf.ui.gallery.PictureGalleryActivity;

/**
 * Artur
 */
public class DisplayBookActivity extends AppCompatActivity {
    private static final String TAG = "DISPLAY_BOOK";
    private static final int REQUEST_IMAGE_CAPTURE = 9002;
    private static final int REQUEST_EXTERNAL_STORAGE = 9003;
    private static final int OPEN_GALLERY_VIEW = 9004;
    public static final String PICTURE_STORAGE_PATH = "storagePath";

    File pictureFile = null;


    private BookPictureStorage bookPictureStorage;

    String bookId = "";

    TextView title, subtitle, author, isbn, publishedDate, nrPages, nrPictures;
    ImageView cover;
    BookViewModel bookViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaybook);


        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {
            bookId = bundle.getString("id");
        }

        Book book = bookViewModel.findBookById(bookId);
        bookPictureStorage = new BookPictureStorage(getApplicationContext(), book);

        setTitle(book.getTitle());

        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        author = findViewById(R.id.author);
        isbn = findViewById(R.id.isbn);
        publishedDate = findViewById(R.id.publishedDate);
        nrPages = findViewById(R.id.nrPages);
        cover = findViewById(R.id.imageView);
        nrPictures = findViewById(R.id.nrOfPictures);

        title.setText(book.getTitle());
        //subtitle.setText("");
        author.setText(book.getAuthor());
        isbn.setText(book.getISBN());
        publishedDate.setText(book.getPublishDate());
        nrPages.setText(String.valueOf(book.getNumberOfPages()));
        nrPictures.setText(String.format("%s: %s", getString(R.string.number_of_pictures), String.valueOf(bookPictureStorage.getNumberOfPictures())));

        FloatingActionButton cameraButton = (FloatingActionButton) findViewById(R.id.take_picture);
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int rc = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if (rc == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        dispatchTakePictureIntent();
                    } else {
                        requestCameraPermission();
                    }
                } else {
                    requestStoragePermission();
                }
            }
        });
    }

    /**
     * Handles the requesting of the storage permission.
     */
    private void requestStoragePermission() {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission_group.STORAGE)) {
            Log.w(TAG, "Storage permission is not granted. Requesting permission");

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    /**
     * Handles the requesting of the storage permission.
     */
    private void requestCameraPermission() {

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.CAMERA)) {
            Log.w(TAG, "Camera permission is not granted. Requesting permission");

            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
    }



    private void dispatchTakePictureIntent() {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    pictureFile = bookPictureStorage.createBookPictureFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (pictureFile != null) {
                    Uri photoURI = FileProvider.getUriForFile(this,
                            "de.unipassau.android.bookshelf.fileprovider",
                            pictureFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode != RESULT_OK && pictureFile != null) {
                pictureFile.delete();
            }
        }
        // update picture count
        nrPictures.setText(String.format("%s: %s", getString(R.string.number_of_pictures), String.valueOf(bookPictureStorage.getNumberOfPictures())));
    }




    public void viewGallery(View v){
        int rc = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(DisplayBookActivity.this, PictureGalleryActivity.class);
            intent.putExtra(PICTURE_STORAGE_PATH, bookPictureStorage.getFolderPath());
            startActivityForResult(intent, OPEN_GALLERY_VIEW);
        } else {
            requestStoragePermission();
        }

    }

    public void refreshLocation(View v){
        Toast.makeText(this, "Refresh Location", Toast.LENGTH_SHORT).show();
    }




    }



