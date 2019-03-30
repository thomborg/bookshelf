package de.unipassau.android.bookshelf.ui.gallery;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageView;

import com.github.chrisbanes.photoview.PhotoView;

import androidx.appcompat.app.AppCompatActivity;
import de.unipassau.android.bookshelf.R;

/**
 * Activity um ein Bild in Fullscreen azuzeigen. Benutzt die 3rd Party Library PhotoView für Pinch-To-Zoom Interaktionen.
 */
public class ShowPictureFullScreenActivity extends AppCompatActivity {
    private PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen);

        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        String path = extras.getString("image");

        photoView = findViewById(R.id.imageView);
        Bitmap imageBitmap = BitmapFactory.decodeFile(path);
        photoView.setImageBitmap(imageBitmap);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        photoView.setAdjustViewBounds(false);
        photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);

    }
}
