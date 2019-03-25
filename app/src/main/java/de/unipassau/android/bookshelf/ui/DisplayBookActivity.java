package de.unipassau.android.bookshelf.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProviders;
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


    TextView title, subtitle, author, isbn, publishedDate, nrPages, nrPictures;
    ImageView cover;
    BookViewModel bookViewModel;

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

        Book book = bookViewModel.findBookById(bookId);

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
        nrPictures.setText("9 Fotos verfügbar");
        //nrPictures.setText(String.valueOf(sampleArrayListNoProductionPls.size()));

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

    public void viewGallery(View v){
        Toast.makeText(this, "View Gallery", Toast.LENGTH_SHORT).show();
    }

    public void refreshLocation(View v){
        Toast.makeText(this, "Refresh Location", Toast.LENGTH_SHORT).show();
    }


}
