package de.unipassau.android.bookshelf.model;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

        public class BookLocation extends Context {

            Double longitude;
            Double latitude;
            List<Address>addresses;

        public void getLocation() {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0);



            } catch (IOException e){
                e.printStackTrace();

        }
        }
        }