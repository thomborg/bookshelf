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
 * AsynTask, der die Kommunikation mit der Google API steuert
 */
public class BookApiClient extends AsyncTask<String, Void, JSONObject> {
    private static final String API_BASE_URL_ISBN = "https://www.googleapis.com/books/v1/volumes?q=isbn:";
    private static final String REQUEST_METHOD = "GET";
    private static final int TIMEOUT = 8000; //8 seconds

    /**
     *
     * @param isbn Wird vom Barcode-Scanner übergeben.
     *             Ist immer eine ISBN-Nummer.
     *             Diese wird an die URL der GoogleAPI angehängt und so ein JSON-Objekt angefordert
     * @return gültiges JSONObject, wenn die Internetverbindung geklappt hat
     *                        null, wenn es Probleme mit der Verbindung gab
     */
    @Override
    protected JSONObject doInBackground(String... isbn) {
        try {
            HttpsURLConnection connection = null;
            try {
                URL url = new URL(API_BASE_URL_ISBN + isbn[0]);
                connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(TIMEOUT);
                connection.setConnectTimeout(TIMEOUT);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (connection != null) {
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
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
