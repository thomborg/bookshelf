package de.unipassau.android.bookshelf.model;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.ui.DisplayBookActivity;

/**
 * Custom Adapter für Book-Objekte
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
    private List<Book> mBooks;
    private BookViewModel bookViewModel;

    public BookListAdapter(Context context) {
        this.context = context;
        mInflater = LayoutInflater.from(context);
        bookViewModel = ViewModelProviders.of((FragmentActivity) context).get(BookViewModel.class);
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
        if (mBooks.get(i).getUrlThumbnail() != null && !mBooks.get(i).getUrlThumbnail().isEmpty()) {
            Picasso.get().load(mBooks.get(i).getUrlThumbnail())
                    .placeholder(R.drawable.ic_library_books_black_placeholder)
                    .error(R.drawable.ic_library_books_black_placeholder)
                    .into(holder.cover);
        } else {
            holder.cover.setImageResource(R.drawable.ic_library_books_black_placeholder);
        }

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, DisplayBookActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("id", mBooks.get(holder.getAdapterPosition()).getId());
                i.putExtras(bundle);
                context.startActivity(i);
            }
        });
        holder.layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final AlertDialog noticeDialog = new AlertDialog.Builder(context).create();
                noticeDialog.setTitle(context.getString(R.string.delete_book));
                noticeDialog.setMessage(
                        String.format("%s" + mBooks.get(holder.getAdapterPosition()).getTitle() + "%s"
                                , context.getString(R.string.delete_hint_1)
                                , context.getString(R.string.delete_hint_2)));
                noticeDialog.setCancelable(true);
                noticeDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        bookViewModel.delete(mBooks.get(holder.getAdapterPosition()));
                        notifyItemRemoved(holder.getAdapterPosition());
                        noticeDialog.dismiss();
                    }
                });
                noticeDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getString(R.string.no), new DialogInterface.OnClickListener() {
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

    @Override
    public int getItemCount() {
        if (mBooks != null)
            return mBooks.size();
        else return 0;
    }
}



