package de.unipassau.android.bookshelf;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
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

    Adapter adapter;
    List<BookResult> list;
    private static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        RecyclerView recyclerView = findViewById(R.id.booksRecyclerView);
        final BookListAdapter adapter = new BookListAdapter(this);
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

/*
        RecyclerView recyclerView = findViewById(R.id.booksRecyclerView);
        list = new ArrayList<>();
        adapter = new Adapter(list);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(llm);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, llm.getOrientation()));
        list.add(new BookResult("Artur", "Hallo", ""));
        list.add(new BookResult("Thomas", "Tschüss", ""));
        adapter.notifyDataSetChanged();
*/
        Spinner shelfSpinner = findViewById(R.id.shelfSpinner);
        List<String> shelfs = new ArrayList<>();
        shelfs.add("Shelf 1");
        shelfs.add("Shelf 2");
        shelfSpinner.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, shelfs));

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
                Log.d(TAG, "Error with Barcode Intend");
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
                if(jsonObject.has("totalItems") && jsonObject.getInt("totalItems")==0)
                    Log.d(TAG, "No Items found!");
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
                        Log.d(TAG, result.getAuthors());
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }


            Book book_code = new Book(result.getAuthors(), result.getTitle(), result.getIsbn10(), result.getPages(), result.getPublishedDate(), result.getThumbnail());
            mBookViewModel.insert(book_code);

            /*
            list.add(new BookResult(result.getAuthors(), result.getTitle(), result.getThumbnail()));
            adapter.notifyItemChanged(list.size()-1);
            */
            super.onPostExecute(jsonObject);
        }
    }

    public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder>{

        List<BookResult> bookResults;

        Adapter(List<BookResult> bookResults){
            this.bookResults = bookResults;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.book_recycler_item, viewGroup, false);
            return new Adapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            viewHolder.title.setText(bookResults.get(i).getTitel());
            viewHolder.author.setText(bookResults.get(i).getAuthor());
            if(!bookResults.get(i).getThumbnail().isEmpty()){
                Picasso.get().load(bookResults.get(i).getThumbnail())
                        .placeholder(R.drawable.ic_launcher_foreground)
                        .error(R.drawable.ic_flash_off) //TODO richtige drawables
                        .into(viewHolder.cover);
            }
            else{
                viewHolder.cover.setImageResource(R.drawable.ic_library_books_black_24dp); //TODO hier auch
            }

            //TODO richtigen Eintrag bei Click ansprechen
            viewHolder.layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(MainActivity.this, DisplayBookActivity.class);
                    startActivity(i);
                }
            });
            viewHolder.layout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final AlertDialog noticeDialog = new AlertDialog.Builder(MainActivity.this).create();
                    noticeDialog.setTitle("Eintrag löschen?");
                    noticeDialog.setMessage("Möchten Sie den Eintrag " + bookResults.get(viewHolder.getAdapterPosition()).getTitel() + " löschen?");
                    noticeDialog.setCancelable(true);
                    noticeDialog.setButton(DialogInterface.BUTTON_POSITIVE, "JA", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String show = list.remove(viewHolder.getAdapterPosition()).getTitel();
                            Toast.makeText(MainActivity.this, show, Toast.LENGTH_SHORT).show();
                            adapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                            noticeDialog.dismiss();
                        }
                    });
                    noticeDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "NEIN", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            noticeDialog.dismiss();
                        }
                    });

                    noticeDialog.show();
                    return false;
                }
            });
        }

        @Override
        public int getItemCount() {
            return bookResults.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder{

            TextView author, title;
            ImageView cover;
            ConstraintLayout layout;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                author = itemView.findViewById(R.id.author);
                title = itemView.findViewById(R.id.title);
                cover = itemView.findViewById(R.id.cover);
                layout = itemView.findViewById(R.id.book_item_layout);
            }
        }

    }

    class BookResult{

        String author, titel, thumbnail;

        public BookResult(String author, String titel, String thumbnail) {
            if(author!=null) this.author = author;
            else this.author = "";
            if(titel!=null) this.titel = titel;
            else this.titel = "";
            if(thumbnail!=null) this.thumbnail = thumbnail;
            else this.thumbnail = "";
        }

        public String getAuthor() {
            return author;
        }

        public String getTitel() {
            return titel;
        }

        public String getThumbnail() {
            return thumbnail;
        }
    }
}
