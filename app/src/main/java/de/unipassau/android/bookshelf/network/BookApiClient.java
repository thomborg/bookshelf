package de.unipassau.android.bookshelf.network;


import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;


/**
 * Artur
 */
public class BookApiClient extends AsyncTask<String, Void, JSONObject> {
    private static final String API_BASE_URL_ISBN = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private static final int TIMEOUT = 8000; //8 seconds
    private static final String TAG = "BookApiClient";

    @Override
    protected JSONObject doInBackground(String... isbn) {
        try {
            HttpsURLConnection connection = null;
            try {
                URL url = new URL(API_BASE_URL_ISBN + isbn[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setReadTimeout(TIMEOUT);
                connection.setConnectTimeout(TIMEOUT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line = reader.readLine();
            while (line != null) {
                builder.append(line);
                line = reader.readLine();
            }
            String response = builder.toString();
            JSONObject jsonObject = new JSONObject(response);
            connection.disconnect();
            return jsonObject;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }



    /*protected boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }*/
}
