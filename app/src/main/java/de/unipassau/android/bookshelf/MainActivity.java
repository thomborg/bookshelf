package de.unipassau.android.bookshelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.unipassau.android.bookshelf.model.Book;
import de.unipassau.android.bookshelf.model.BookListAdapter;
import de.unipassau.android.bookshelf.model.BookViewModel;
import de.unipassau.android.bookshelf.network.BookApiClient;
import de.unipassau.android.bookshelf.network.ResultDTO;
import de.unipassau.android.bookshelf.ui.DisplayBookActivity;
import de.unipassau.android.bookshelf.ui.SettingsActivity;
import de.unipassau.android.bookshelf.ui.barcodereader.BarcodeScanActivity;

public class MainActivity extends AppCompatActivity {
    private static final int RC_BARCODE_CAPTURE = 9001;
    private BookViewModel mBookViewModel;
    BookListAdapter adapter;
    ArrayAdapter shelfAdapter;
    String[] shelfArray;
    Spinner shelfSpinner;

    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "onCreate: ");


        RecyclerView recyclerView = findViewById(R.id.booksRecyclerView);
        adapter = new BookListAdapter(this);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, llm.getOrientation()));

        mBookViewModel = ViewModelProviders.of(this).get(BookViewModel.class);

        mBookViewModel.getAllBooks().observe(this, new Observer<List<Book>>() {
            @Override
            public void onChanged(List<Book> books) {
                adapter.setBooks(books);
            }
        });

        shelfSpinner = findViewById(R.id.shelfSpinner);


        shelfSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                showShelf(shelfArray[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, BarcodeScanActivity.class);

                startActivityForResult(intent, RC_BARCODE_CAPTURE);
            }

        });
    }

    @Override
    protected void onStart() {

        Log.d(TAG, "onStart: ");
        String[] tmp = mBookViewModel.getAllShelfs();
        shelfArray = new String[tmp.length+1];
        for(int i =0; i<tmp.length; i++) {
            Log.d(TAG, tmp[i]);
            shelfArray[i] = tmp[i];
        }
        shelfArray[shelfArray.length-1] = "-Show all Books-";

        shelfAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, shelfArray);
        shelfSpinner.setAdapter(shelfAdapter);

        super.onStart();
    }

    @Override
    protected void onStop() {

        super.onStop();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeScanActivity.BarcodeObject);
                    Log.d(TAG, "Barcode read: " + barcode.displayValue);
                    // Send request
                    MainAsyncTask client = new MainAsyncTask();
                    client.execute(barcode.displayValue);
                } else {
                    Log.d(TAG, "No barcode captured, intent data is null");
                }
            } else {
                // Error
                Log.d(TAG, "Error with Barcode Intent");
            }
        }
        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public class MainAsyncTask extends BookApiClient{

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            JSONObject info = null;
            JSONArray items = null;
            ResultDTO result = new ResultDTO();
            try {
                if(jsonObject.has("totalItems") && jsonObject.getInt("totalItems")==0){
                    Log.d(TAG, "No Items found!");
                    final android.app.AlertDialog noticeDialog = new AlertDialog.Builder(MainActivity.this).create();
                    noticeDialog.setTitle("Fehler!");
                    noticeDialog.setMessage("Zu dieser ISBN wurde leider kein Eintrag in unserer Datenbank gefunden");
                    noticeDialog.setCancelable(true);
                    noticeDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            noticeDialog.dismiss();
                        }
                    });
                    noticeDialog.show();
                }
                else {
                    if (jsonObject.has("items"))
                        items = jsonObject.getJSONArray("items");
                    if (items.getJSONObject(0).has("volumeInfo"))
                        info = items.getJSONObject(0).getJSONObject("volumeInfo");
                    if (info.has("title"))
                        result.setTitle(info.getString("title"));
                    if (info.has("subtitle"))
                        result.setSubtitle(info.getString("subtitle"));
                    if (info.has("publisher"))
                        result.setPublisher(info.getString("publisher"));
                    if (info.has("publishedDate"))
                        result.setPublishedDate(info.getString("publishedDate"));
                    if(info.has("industryIdentifiers")){
                        JSONArray isbnArray = info.getJSONArray("industryIdentifiers");
                        for(int i = 0; i < isbnArray.length(); i++){
                            JSONObject o = (JSONObject) isbnArray.get(i);
                            if(o.has("type") && o.getString("type").equals("ISBN_13"))
                                result.setIsbn13(o.getString("identifier"));
                        }
                    }
                    if (info.has("imageLinks"))
                        result.setThumbnail(info.getJSONObject("imageLinks").getString("thumbnail"));
                    if(info.has("pageCount"))
                        result.setPages(info.getInt("pageCount"));
                    StringBuilder authors = new StringBuilder();
                    JSONArray authorArray;
                    if (info.has("authors")) {
                        authorArray = info.getJSONArray("authors");
                        for (int i = 0; i < authorArray.length(); i++) {
                            authors.append(authorArray.get(i));
                            if (i != authorArray.length() - 1)
                                authors.append(", ");
                        }
                        result.setAuthors(authors.toString());

                        Book book_code = new Book(result.getAuthors(), result.getTitle(), result.getIsbn13(), result.getPages(), result.getPublishedDate(), result.getThumbnail());
                        mBookViewModel.insert(book_code);

                        Intent i = new Intent(MainActivity.this, DisplayBookActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("id", book_code.getId());
                        i.putExtras(bundle);
                        startActivity(i);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }




            /*
            list.add(new BookResult(result.getAuthors(), result.getTitle(), result.getThumbnail()));
            adapter.notifyItemChanged(list.size()-1);
            */
            super.onPostExecute(jsonObject);
        }
    }

    protected void showShelf(String shelfToShow){
        if ("-Show all Books-".equals(shelfToShow)) {
            adapter.setBooks(mBookViewModel.getAllBooks().getValue());
        }
        else {
            List<Book> tmp = mBookViewModel.getAllBooks().getValue();
            List<Book> booksToShow = new ArrayList<>();
            for (int i = 0; i < tmp.size(); i++)
                if (tmp.get(i).getShelf() != null && tmp.get(i).getShelf().equals(shelfToShow))
                    booksToShow.add(tmp.get(i));

            adapter.setBooks(booksToShow);

        }
    }
}
