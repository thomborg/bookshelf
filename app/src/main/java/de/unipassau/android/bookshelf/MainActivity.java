package de.unipassau.android.bookshelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.unipassau.android.bookshelf.model.Book;
import de.unipassau.android.bookshelf.model.BookListAdapter;
import de.unipassau.android.bookshelf.model.BookViewModel;
import de.unipassau.android.bookshelf.network.BookApiClient;
import de.unipassau.android.bookshelf.network.ResultDTO;
import de.unipassau.android.bookshelf.ui.DisplayBookActivity;
import de.unipassau.android.bookshelf.ui.barcodereader.BarcodeScanActivity;

/**
 * MainActivity: Anzeige der verschiedenen Bücher in einer Liste, Möglichkeit nach Regalen zu sortieren.
 */
public class MainActivity extends AppCompatActivity {
    private static final int RC_BARCODE_CAPTURE = 9001;
    private static final String JSON_KEY_TITLE = "title";
    private static final String JSON_KEY_AUTHOR = "authors";
    private static final String JSON_KEY_INFO = "volumeInfo";
    private static final String JSON_KEY_TOTALITEMS = "totalItems";
    private static final String JSON_KEY_ITEMS = "items";
    private static final String JSON_KEY_PUBLISHEDDATE = "publishedDate";
    private static final String JSON_KEY_INDIDENT = "industryIdentifiers";
    private static final String JSON_KEY_TYPE = "type";
    private static final String JSON_KEY_ISBN13 = "ISBN_13";
    private static final String JSON_KEY_IDENTIFIER = "identifier";
    private static final String JSON_KEY_IMAGELINKS = "imageLinks";
    private static final String JSON_KEY_THUMBNAIL = "thumbnail";
    private static final String JSON_KEY_PAGECOUNT = "pageCount";
    private BookViewModel mBookViewModel;
    private BookListAdapter adapter;
    private ArrayAdapter shelfAdapter;
    private String[] shelfArray;
    private Spinner shelfSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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
        String[] tmp = mBookViewModel.getAllShelfs();
        if (tmp != null) {
            shelfArray = new String[tmp.length + 1];
            System.arraycopy(tmp, 0, shelfArray, 0, tmp.length);
            shelfArray[shelfArray.length - 1] = getString(R.string.show_all_books);
            shelfAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, shelfArray);
            shelfSpinner.setAdapter(shelfAdapter);
        }


        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_BARCODE_CAPTURE) {
            if (resultCode == CommonStatusCodes.SUCCESS) {
                if (data != null) {
                    Barcode barcode = data.getParcelableExtra(BarcodeScanActivity.BarcodeObject);
                    // Send request
                    MainAsyncTask client = new MainAsyncTask();
                    client.execute(barcode.displayValue);
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public class MainAsyncTask extends BookApiClient {

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (jsonObject == null) {
                final android.app.AlertDialog noticeDialog = new AlertDialog.Builder(MainActivity.this).create();
                noticeDialog.setTitle(getString(R.string.error));
                noticeDialog.setMessage(getString(R.string.check_internet_connection));
                noticeDialog.setCancelable(true);
                noticeDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        noticeDialog.dismiss();
                    }
                });
                noticeDialog.show();
                return;
            }
            JSONObject info = null;
            JSONArray items = null;
            ResultDTO result = new ResultDTO();
            try {
                if (jsonObject.has(JSON_KEY_TOTALITEMS) && jsonObject.getInt(JSON_KEY_TOTALITEMS) == 0) {
                    final android.app.AlertDialog noticeDialog = new AlertDialog.Builder(MainActivity.this).create();
                    noticeDialog.setTitle(getString(R.string.error));
                    noticeDialog.setMessage(getString(R.string.no_book_found));
                    noticeDialog.setCancelable(true);
                    noticeDialog.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            noticeDialog.dismiss();
                        }
                    });
                    noticeDialog.show();
                } else {
                    if (jsonObject.has(JSON_KEY_ITEMS))
                        items = jsonObject.getJSONArray(JSON_KEY_ITEMS);
                    if (items.getJSONObject(0).has(JSON_KEY_INFO))
                        info = items.getJSONObject(0).getJSONObject(JSON_KEY_INFO);
                    if (info.has(JSON_KEY_TITLE))
                        result.setTitle(info.getString(JSON_KEY_TITLE));
                    if (info.has(JSON_KEY_PUBLISHEDDATE))
                        result.setPublishedDate(info.getString(JSON_KEY_PUBLISHEDDATE));
                    if (info.has(JSON_KEY_INDIDENT)) {
                        JSONArray isbnArray = info.getJSONArray(JSON_KEY_INDIDENT);
                        for (int i = 0; i < isbnArray.length(); i++) {
                            JSONObject o = (JSONObject) isbnArray.get(i);
                            if (o.has(JSON_KEY_TYPE) && o.getString(JSON_KEY_TYPE).equals(JSON_KEY_ISBN13))
                                result.setIsbn13(o.getString(JSON_KEY_IDENTIFIER));
                        }
                    }
                    if (info.has(JSON_KEY_IMAGELINKS))
                        result.setThumbnail(info.getJSONObject(JSON_KEY_IMAGELINKS).getString(JSON_KEY_THUMBNAIL));
                    if (info.has(JSON_KEY_PAGECOUNT))
                        result.setPages(info.getInt(JSON_KEY_PAGECOUNT));
                    StringBuilder authors = new StringBuilder();
                    JSONArray authorArray;
                    if (info.has(JSON_KEY_AUTHOR)) {
                        authorArray = info.getJSONArray(JSON_KEY_AUTHOR);
                        for (int i = 0; i < authorArray.length(); i++) {
                            authors.append(authorArray.get(i));
                            if (i != authorArray.length() - 1)
                                authors.append(", ");
                        }
                        result.setAuthors(authors.toString());

                    }
                    Book book_code = new Book(result.getAuthors(), result.getTitle(), result.getIsbn13(), result.getPages(), result.getPublishedDate(), result.getThumbnail());
                    mBookViewModel.insert(book_code);

                    Intent i = new Intent(MainActivity.this, DisplayBookActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("id", book_code.getId());
                    i.putExtras(bundle);
                    startActivity(i);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

            super.onPostExecute(jsonObject);
        }
    }

    protected void showShelf(String shelfToShow) {
        if (shelfToShow.equals(getString(R.string.show_all_books))) {
            adapter.setBooks(mBookViewModel.getAllBooks().getValue());
        } else {
            List<Book> tmp = mBookViewModel.getAllBooks().getValue();
            if (tmp != null) {
                List<Book> booksToShow = new ArrayList<>();
                for (int i = 0; i < tmp.size(); i++)
                    if (tmp.get(i).getShelf() != null && tmp.get(i).getShelf().equals(shelfToShow))
                        booksToShow.add(tmp.get(i));
                adapter.setBooks(booksToShow);
            }
        }
    }
}
