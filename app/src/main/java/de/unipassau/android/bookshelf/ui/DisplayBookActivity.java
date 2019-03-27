package de.unipassau.android.bookshelf.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
import de.unipassau.android.bookshelf.MainActivity;
import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.database.BookDAO;
import de.unipassau.android.bookshelf.model.Book;
import de.unipassau.android.bookshelf.model.BookPicture;
import de.unipassau.android.bookshelf.model.BookViewModel;

/**
 * Artur
 */
public class DisplayBookActivity extends AppCompatActivity {
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private ArrayList<BookPicture> sampleArrayListNoProductionPls;


    TextView title, subtitle, author, isbn, publishedDate, nrPages, nrPictures, shelf;
    ImageView cover;
    NumberPicker picker;
    BookViewModel bookViewModel;
    Book book;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaybook);

        sampleArrayListNoProductionPls = new ArrayList<BookPicture>();

        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        Bundle bundle = getIntent().getExtras();
        String bookId = "";
        if (bundle != null) {
            bookId = bundle.getString("id");
        }

        book = bookViewModel.findBookById(bookId);

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
        nrPictures.setText("9");
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
                dispatchTakePictureIntent();
            }
        });

        if(shelf.getText().toString().isEmpty())
            Snackbar.make(findViewById(android.R.id.content), "Ihr Buch steht noch in keinem Regal!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Eintragen", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            changeShelf(view);
                        }
                    })
                    .setActionTextColor(getResources().getColor(android.R.color.holo_red_light ))
                    .show();
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

    public void viewGallery(View v){
        Toast.makeText(this, "View Gallery", Toast.LENGTH_SHORT).show();
    }

    public void refreshLocation(View v){
        Toast.makeText(this, "Refresh Location", Toast.LENGTH_SHORT).show();
    }

    public void changeShelf(View v){
        //Toast.makeText(this, "Change Shlef", Toast.LENGTH_SHORT).show();
        if(picker.getVisibility()==View.VISIBLE)
            picker.setVisibility(View.GONE);
        else {
            picker.setVisibility(View.VISIBLE);

            final String[] shelfs = bookViewModel.getAllShelfs();

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
