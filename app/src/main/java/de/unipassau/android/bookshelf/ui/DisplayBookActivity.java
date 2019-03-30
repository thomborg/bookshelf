package de.unipassau.android.bookshelf.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.provider.MediaStore;
import androidx.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.location.FetchAddressIntentService;
import de.unipassau.android.bookshelf.location.LocationConstants;
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
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9005;
    private static final int ALL_PERMISSIONS_RESULT = 9006;

    public static final String PICTURE_STORAGE_PATH = "storagePath";


    private GoogleApiClient googleApiClient;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private Location lastKnownLocation;
    private AddressResultReceiver resultReceiver;
    private FusedLocationProviderClient fusedLocationClient;



    File pictureFile = null;
    private BookPictureStorage bookPictureStorage;

    String bookId = "";
    TextView title, author, isbn, publishedDate, nrPages, nrPictures, shelf, location;
    ImageView cover;
    NumberPicker picker;
    BookViewModel bookViewModel;
    Book book;
    Snackbar snackbar;

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_displaybook);
        bookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);


        // Permission
        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        permissions.add(Manifest.permission.CAMERA);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }

        // Location
        resultReceiver = new AddressResultReceiver(new Handler());
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            lastKnownLocation = location;
                        }
                    }
                });


        // Intent
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
        location = findViewById(R.id.location);

        title.setText(book.getTitle());
        author.setText(book.getAuthor());
        isbn.setText(book.getISBN());
        publishedDate.setText(book.getPublishDate());
        nrPages.setText(String.valueOf(book.getNumberOfPages()));
        nrPictures.setText(String.valueOf(bookPictureStorage.getNumberOfPictures()));
        shelf.setText(book.getShelf());
        if (book.getLocation() != null) {
            location.setText(book.getLocation());
        } else {
            location.setText(R.string.no_address);
        }
        if (book.getUrlThumbnail() != null && !book.getUrlThumbnail().isEmpty()) {
            Picasso.get().load(book.getUrlThumbnail())
                    .placeholder(R.drawable.ic_library_books_black_placeholder)
                    .error(R.drawable.ic_library_books_black_placeholder)
                    .into(cover);
        } else {
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
                .setActionTextColor(getResources().getColor(android.R.color.holo_red_light));
        if (shelf.getText().toString().isEmpty())
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

            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
        }
    }


    /**
     * Requests the camera to capture a image.
     */
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

    public void viewGallery(View v) {
        int rc = ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE);
        if (rc == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent(DisplayBookActivity.this, PictureGalleryActivity.class);
            intent.putExtra(PICTURE_STORAGE_PATH, bookPictureStorage.getFolderPath());
            startActivityForResult(intent, OPEN_GALLERY_VIEW);
        } else {
            requestStoragePermission();
        }

    }

    /**
     * onClick-Methode des addLocation-Buttons
     * @param v geklickter Button
     */
    public void refreshLocation(View v) {

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Toast.makeText(this, "Refresh Location", Toast.LENGTH_SHORT).show();


        startIntentService();
        fetchAddressButtonHandler(v);

    }

    /**
     * onClick-Methode des changeShelf-Buttons
     * @param v geklickter Button
     */
    public void changeShelf(View v) {
        snackbar.dismiss();

        final String[] shelfs = bookViewModel.getAllShelfs();
        if (shelfs.length == 0) addShelf(v);
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

    /**
     * onClick-Methode des addShelf-Buttons
     * @param v geklickter Button
     */
    public void addShelf(View v) {
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

    private ArrayList<String> permissionsToRequest(ArrayList<String> wantedPermissions) {
        ArrayList<String> result = new ArrayList<>();

        for (String perm : wantedPermissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }

        return result;
    }

    private boolean hasPermission(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
        }

        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!checkPlayServices()) {
            Toast.makeText(this, R.string.you_need_play_services, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // stop location updates
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }


    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST);
            } else {
                finish();
            }

            return false;
        }

        return true;
    }


    private void updateAddress() {
        location.setText(book.getLocation());
    }

    private void fetchAddressButtonHandler(View view) {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {

                    @Override
                    public void onSuccess(Location location) {
                        lastKnownLocation = location;

                        // In some rare cases the location returned can be null
                        if (lastKnownLocation == null) {
                            return;
                        }

                        if (!Geocoder.isPresent()) {
                            Toast.makeText(DisplayBookActivity.this, R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
                            return;
                        }

                        // Start service and update UI to reflect new location
                        startIntentService();
                    }
                });
    }


    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(LocationConstants.RECEIVER, resultReceiver);
        intent.putExtra(LocationConstants.LOCATION_DATA_EXTRA, lastKnownLocation);
        startService(intent);
    }

    private void showAddressToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }


    class AddressResultReceiver extends ResultReceiver {
        String addressOutput;

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if (resultData == null) {
                return;
            }

            // Display the address string
            // or an error message sent from the intent service.
            addressOutput = resultData.getString(LocationConstants.RESULT_DATA_KEY);
            if (addressOutput == null) {
                addressOutput = getString(R.string.no_address_found);
            }

            // Show a toast message if an address was found.
            if (resultCode == LocationConstants.SUCCESS_RESULT) {
                bookViewModel.setLocation(book.getId(), addressOutput);
                book.setLocation(addressOutput);
                updateAddress();
                showAddressToast(getString(R.string.address_found));
            }

        }

    }
}
