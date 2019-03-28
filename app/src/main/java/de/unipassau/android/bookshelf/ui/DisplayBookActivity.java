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
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
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
import de.unipassau.android.bookshelf.model.Book;
import de.unipassau.android.bookshelf.model.BookViewModel;
import de.unipassau.android.bookshelf.ui.gallery.BookPictureStorage;
import de.unipassau.android.bookshelf.ui.gallery.PictureGalleryActivity;

/**
 * Anzeige einzelner Bücher mit genauerer Information
 * Möglichkeit Location hinzuzufügen, Regale zu verwalten, Fotos von diesen Büchern aufzunehmen und zu speichern
 */
public class DisplayBookActivity extends AppCompatActivity {
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
        nrPictures.setText(String.valueOf(bookPictureStorage.getNumberOfPictures()));
        shelf.setText(book.getShelf());
        if(book.getUrlThumbnail()!=null && !book.getUrlThumbnail().isEmpty()){
            Picasso.get().load(book.getUrlThumbnail())
                    .placeholder(R.drawable.ic_library_books_black_placeholder)
                    .error(R.drawable.ic_library_books_black_placeholder)
                    .into(cover);
        }
        else{
            cover.setImageResource(R.drawable.ic_library_books_black_placeholder);
        }

        FloatingActionButton cameraButton = findViewById(R.id.take_picture);
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

        snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.displaybook_snackbar_hint), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(R.string.displaybook_snackbar_action), new View.OnClickListener() {
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
        nrPictures.setText(String.valueOf(bookPictureStorage.getNumberOfPictures()));
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

        final String[] shelfs = bookViewModel.getAllShelfs();
        if(shelfs.length==0) addShelf(v);
        else {
            AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            final View dialogView = inflater.inflate(R.layout.custom_table, null);
            dialogBuilder.setView(dialogView);
            dialogBuilder.setTitle(R.string.choose_shelf);
            final AlertDialog b = dialogBuilder.create();

            ListView listView = dialogView.findViewById(R.id.listView);
            ArrayAdapter listAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, shelfs);
            listView.setAdapter(listAdapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    bookViewModel.setShelf(book.getId(), shelfs[position]);
                    shelf.setText(shelfs[position]);
                    b.dismiss();
                }
            });
            b.show();
        }

    }

    public void addShelf(View v){
        snackbar.dismiss();

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_dialog, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = dialogView.findViewById(R.id.editText);

        dialogBuilder.setTitle(R.string.add_shelf);
        dialogBuilder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                bookViewModel.setShelf(book.getId(), edt.getText().toString());
                shelf.setText(edt.getText().toString());
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


}
