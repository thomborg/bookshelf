package de.unipassau.android.bookshelf.ui.gallery;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import de.unipassau.android.bookshelf.model.Book;

/**
 * Klasse um die Bilder die zu einem Buch gehören zu verwalten. Erstellt ein privates Album im Speicher der App.
 */
public class BookPictureStorage {
    private static final String LOG = "BookPicture Store";

    private static final String BOOKSHELF_PREFIX = "BookPicture_";
    private final File folderPath;


    public BookPictureStorage(Context context, Book book) {
        folderPath = getPrivateAlbumStorageDir(context, book.getId());
    }

    private File getPrivateAlbumStorageDir(Context context, String albumName) {
        File file = new File(context.getExternalFilesDir(
                Environment.DIRECTORY_PICTURES), albumName);
        if (!file.mkdirs()) {
            Log.i(LOG, "Directory was created");
        }
        return file;
    }

    public File createBookPictureFile() throws IOException {
        return File.createTempFile(
                BOOKSHELF_PREFIX + UUID.randomUUID(),
                ".jpg",
                folderPath
        );
    }

    public String getFolderPath() {
        return folderPath.getAbsolutePath();
    }

    public int getNumberOfPictures() {
        if (folderPath.isDirectory()) {
            File[] fileList = folderPath.listFiles();
            return fileList.length;
        } else
            return 0;
    }


}


