package de.unipassau.android.bookshelf.model;

import android.app.AlertDialog;
import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.unipassau.android.bookshelf.R;
import de.unipassau.android.bookshelf.ui.gallery.ShowPictureFullScreenActivity;

public class BookPictureAdapter extends RecyclerView.Adapter<BookPictureAdapter.ViewHolder> {

    private List<BookPicture> galleryList;

    public BookPictureAdapter(List<BookPicture> galleryList) {
        this.galleryList = galleryList;
    }

    @Override
    public BookPictureAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.picture_tile, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookPictureAdapter.ViewHolder viewHolder, int i) {
        final BookPicture bookPicture = galleryList.get(i);
        viewHolder.img.setScaleType(ImageView.ScaleType.CENTER_CROP);
        viewHolder.img.setImageBitmap(bookPicture.getImageThumbnail());

        viewHolder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ShowPictureFullScreenActivity.class);
                intent.putExtra("image", bookPicture.getPath());
                v.getContext().startActivity(intent);
            }
        });
        viewHolder.img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                        final AlertDialog noticeDialog = new AlertDialog.Builder(v.getContext()).create();
                        noticeDialog.setTitle("Bild löschen?");
                        noticeDialog.setMessage("Möchten Sie das Bild löschen?");
                        noticeDialog.setCancelable(true);
                        noticeDialog.setButton(DialogInterface.BUTTON_POSITIVE, "JA", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (bookPicture.delete()) {
                                    galleryList.remove(bookPicture);
                                    // bookViewModel.delete(mBooks.get(holder.getAdapterPosition()));
                                    notifyItemRemoved(viewHolder.getAdapterPosition());
                                }
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
                        return true;
                    }

        });
    }

    @Override
    public int getItemCount() {
        return galleryList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView img;
        public ViewHolder(View view) {
            super(view);

            img = (ImageView) view.findViewById(R.id.img);
        }
    }
}