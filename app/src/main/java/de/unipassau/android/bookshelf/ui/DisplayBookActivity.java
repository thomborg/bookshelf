package de.unipassau.android.bookshelf.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.database.BookDAO;
import de.unipassau.android.bookshelf.model.Book;
import de.unipassau.android.bookshelf.model.BookPicture;
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

    TextView title, author, isbn, publishedDate, nrPages, nrPictures, shelf;
    ImageView cover;
    NumberPicker picker;
    BookViewModel bookViewModel;
    Book book;
    Snackbar snackbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaybook);
        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        Bundle bundle = getIntent().getExtras();


        if (bundle != null) {
            bookId = bundle.getString("id");
        }

        book = bookViewModel.findBookById(bookId);
        bookPictureStorage = new BookPictureStorage(getApplicationContext(), book);

        setTitle(book.getTitle());

        title = findViewById(R.id.title);
        author = findViewById(R.id.author);
        isbn = findViewById(R.id.isbn);
        publishedDate = findViewById(R.id.publishedDate);
        nrPages = findViewById(R.id.nrPages);
        cover = findViewById(R.id.imageView);
        nrPictures = findViewById(R.id.nrOfPictures);
        shelf = findViewById(R.id.shelf);
        picker = findViewById(R.id.shelfPicker);

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        isbn.setText(book.getISBN());
        publishedDate.setText(book.getPublishDate());
        nrPages.setText(String.valueOf(book.getNumberOfPages()));
        nrPictures.setText(String.format("%s: %s", getString(R.string.number_of_pictures), String.valueOf(bookPictureStorage.getNumberOfPictures())));
        shelf.setText(book.getShelf());
        if(book.getUrlThumbnail()!=null && !book.getUrlThumbnail().isEmpty()){
            Picasso.get().load(book.getUrlThumbnail())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_flash_off) //TODO richtige drawables
                    .into(cover);
        }
        else{
            cover.setImageResource(R.drawable.ic_library_books_black_24dp); //TODO hier auch
        }
        //nrPictures.setText(String.valueOf(sampleArrayListNoProductionPls.size()));

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

        snackbar = Snackbar.make(findViewById(android.R.id.content), "Ihr Buch steht noch in keinem Regal!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Eintragen", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        changeShelf(view);
                    }
                })
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ));
        if(shelf.getText().toString().isEmpty())
            snackbar.show();

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

    public void changeShelf(View v){
        snackbar.dismiss();
        if(picker.getVisibility()==View.VISIBLE)
            picker.setVisibility(View.GONE);
        else {
            picker.setVisibility(View.VISIBLE);

            final String[] shelfs = bookViewModel.getAllShelfs();

            if(shelfs.length==1) {
                bookViewModel.setShelf(book.getId(), shelfs[0]);
                shelf.setText(shelfs[0]);
            }

            picker = findViewById(R.id.shelfPicker);
            picker.setMinValue(0);
            picker.setMaxValue(shelfs.length - 1);
            picker.setDisplayedValues(shelfs);
            picker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);

            picker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                @Override
                public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                    Toast.makeText(DisplayBookActivity.this, shelfs[newVal], Toast.LENGTH_SHORT).show();
                    bookViewModel.setShelf(book.getId(), shelfs[newVal]);
                    shelf.setText(shelfs[newVal]);
                }
            });
        }
    }

    public void addShelf(View v){
        snackbar.dismiss();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = dialogView.findViewById(R.id.editText);

        dialogBuilder.setTitle("Add new Shelf");
        dialogBuilder.setMessage("Enter name below");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                bookViewModel.setShelf(book.getId(), edt.getText().toString());
                shelf.setText(edt.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


}
