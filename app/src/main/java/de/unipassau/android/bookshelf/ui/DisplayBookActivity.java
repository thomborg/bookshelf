package de.unipassau.android.bookshelf.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import de.unipassau.android.bookshelf.R;

/**
 * Artur
 */
public class DisplayBookActivity extends AppCompatActivity {

    TextView title, subtitle, author, isbn, publishedDate, nrPages, nrPhotos;
    ImageView cover;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaybook);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String bookId = bundle.getString("id");
        }

        setTitle("TITEL");

        title = findViewById(R.id.title);
        subtitle = findViewById(R.id.subtitle);
        author = findViewById(R.id.author);
        isbn = findViewById(R.id.isbn);
        publishedDate = findViewById(R.id.publishedDate);
        nrPages = findViewById(R.id.nrPages);
        nrPhotos = findViewById(R.id.nrPhotos);
        cover = findViewById(R.id.imageView);

        title.setText("Titel");
        subtitle.setText("Subtitel");
        author.setText("Autor");
        isbn.setText("ISBN: 93772975738");
        publishedDate.setText("Herausgegeben am 23.03.2019");
        nrPages.setText("Seiten: 128");
        nrPhotos.setText("9 Fotos verfügbar");


    }
}
