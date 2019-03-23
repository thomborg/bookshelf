package de.unipassau.android.bookshelf.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.network.ResultDTO;

/**
 * Artur
 */
public class DisplayBookActivity extends AppCompatActivity {

    TextView title, subtitle, author, isbn, publishedDate, nrPages;
    ImageView cover;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaybook);

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

        title.setText("Hallo");
        subtitle.setText("Tschüss");
        author.setText("Artur");
        isbn.setText("1234567890");
        publishedDate.setText("23.03.2019");
        nrPages.setText("100");


    }
}
