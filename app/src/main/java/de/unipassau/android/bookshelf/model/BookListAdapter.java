package de.unipassau.android.bookshelf.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.ui.DisplayBookActivity;

/**
 * Michi
 */
public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder> {

    class BookViewHolder extends RecyclerView.ViewHolder {
        private final TextView author;
        private final TextView title;
        private final ImageView cover;
        private final ConstraintLayout layout;


        private BookViewHolder(View itemView) {
            super(itemView);
            author = itemView.findViewById(R.id.author);
            title = itemView.findViewById(R.id.title);
            cover = itemView.findViewById(R.id.cover);
            layout = itemView.findViewById(R.id.book_item_layout);
        }
    }

    private final Context context;
    private final LayoutInflater mInflater;
    private List<Book> mBooks; // Cached copy of Books

    public BookListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.book_recycler_item, parent, false);
        return new BookViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final BookViewHolder holder, int i) {
        holder.title.setText(mBooks.get(i).getTitle());
        holder.author.setText(mBooks.get(i).getAuthor());
        if(!mBooks.get(i).getUrlThumbnail().isEmpty()){
            Picasso.get().load(mBooks.get(i).getUrlThumbnail())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_flash_off) //TODO richtige drawables
                    .into(holder.cover);
        }
        else{
            holder.cover.setImageResource(R.drawable.ic_library_books_black_24dp); //TODO hier auch
        }

        //TODO richtigen Eintrag bei Click ansprechen
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DisplayBookActivity.class);
                context.startActivity(i);
            }
        });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog noticeDialog = new AlertDialog.Builder(context).create();
                noticeDialog.setTitle("Eintrag löschen?");
                noticeDialog.setMessage("Möchten Sie den Eintrag " + mBooks.get(holder.getAdapterPosition()).getTitle() + " löschen?");
                noticeDialog.setCancelable(true);
                noticeDialog.setButton(DialogInterface.BUTTON_POSITIVE, "JA", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String show = mBooks.remove(holder.getAdapterPosition()).getTitle();
                        Toast.makeText(context, show, Toast.LENGTH_SHORT).show();
                        notifyItemRemoved(holder.getAdapterPosition());
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


    public void setBooks(List<Book> Books) {
        mBooks = Books;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mBooks has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mBooks != null)
            return mBooks.size();
        else return 0;
    }
}



