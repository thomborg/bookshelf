package de.unipassau.android.bookshelf;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Observable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import de.unipassau.android.bookshelf.Database.BooksDataSource;
import de.unipassau.android.bookshelf.Database.BooksRepository;
import de.unipassau.android.bookshelf.Local.BooksDatabase;
import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.model.Book;
import de.unipassau.android.bookshelf.model.BookDetails;
import de.unipassau.android.bookshelf.ui.Potrait;
import io.reactivex.Flowable;
import io.reactivex.FlowableSubscriber;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.internal.operators.observable.ObservableReplay;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity{
    private ListView listViewBooks;

    public static final int ADD_BOOK_REQUEST = 1;
    public static final int EDIT_BOOK_REQUEST = 2;

    private BooksDataSource noteViewModel;

    //Adapter
        List<Book> booksList = new <Book> ArrayList();
        ArrayAdapter aa;

    //Database
    private CompositeDisposable compositeDisposable;
    private BooksRepository booksRepository;


    private void onGetAllBooksSucess(){}



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewBooks = findViewById(R.id.listViewBooks);
        compositeDisposable = new CompositeDisposable();

        aa = new ArrayAdapter(this, android.R.layout.simple_expandable_list_item_1, booksList);
        registerForContextMenu(listViewBooks);
        listViewBooks.setAdapter(aa);


        BooksDatabase booksDatabase = BooksDatabase.getInstance(this);


        loadData();


        MaterialButton btnScan = (MaterialButton) findViewById(R.id.buttonScan);
        btnScan.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Add new Book by Autor
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(intent, ADD_BOOK_REQUEST);
            }
        });


        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(null);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new Home_Fragment()).commit();



            FloatingActionButton fab = findViewById(R.id.buttonScan);
            fab.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }
        BottomNavigationView.OnNavigationItemSelectedListener navListener =
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;

                        switch (item.getItemId()) {
                            case R.id.nav_home:
                                selectedFragment = new Home_Fragment();
                                break;
                            case R.id.nav_message:
                                selectedFragment = new Favorite_Fragment();
                                break;
                            case R.id.nav_search:
                                selectedFragment = new Search_Fragment();
                                break;
                        }

                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();

                        return true;
                    }
                };

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return true;
            }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                //BooksDataSource.deleteAllBooks();
                Toast.makeText(MainActivity.this, "All notes deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_BOOK_REQUEST && resultCode == RESULT_OK) {
            String author = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            String isbN = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int numberOfpages = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);
            String publishDate = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            String publishPlaces = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            Book book = new Book(author, title, isbN, numberOfpages, publishDate, publishPlaces);
            noteViewModel.insertBooks();

            Toast.makeText(this, "Note saved", Toast.LENGTH_SHORT).show();
        } else if (requestCode == EDIT_BOOK_REQUEST && resultCode == RESULT_OK) {
            int id = data.getIntExtra(AddEditNoteActivity.EXTRA_ID, -1);

            if (id == -1) {
                Toast.makeText(this, "Note can't be updated", Toast.LENGTH_SHORT).show();
                return;
            }

            String author = data.getStringExtra(AddEditNoteActivity.EXTRA_TITLE);
            String title = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            String isbN = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            int numberOfpages = data.getIntExtra(AddEditNoteActivity.EXTRA_PRIORITY, 1);
            String publishDate = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);
            String publishPlaces = data.getStringExtra(AddEditNoteActivity.EXTRA_DESCRIPTION);

            Book book = new Book(author, title, isbN, numberOfpages, publishDate, publishPlaces);
            book.getAuthor();
            noteViewModel.updateBooks();

            Toast.makeText(this, "Note updated", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
        }
    }
    private void loadData() {
        Disposable disposable = booksRepository.getAllBooks().observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io()).subscribe(new Consumer<List<BookDetails>>() {
                                                                                                                                                   @Override
                                                                                                                                                   public void accept(List<BookDetails> bookDetails) throws Exception { onGetAllBooksSucess(booksList);
                                                                                                                                                   }
                                                                                                                                               },
                new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(MainActivity.this, "" + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                });
        compositeDisposable.add(disposable);
    }
        private void onGetAllBooksSucess(List <Book> booksList){
            booksList.clear();
            booksList.addAll(booksList);
            aa.notifyDataSetChanged();

        }



}

