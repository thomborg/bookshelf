package de.unipassau.android.bookshelf.ui;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.util.Log;

import de.unipassau.android.bookshelf.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    SharedPreferences sharedPreferences;

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {

        setPreferencesFromResource(R.xml.preferences, s);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.getActivity());

    }
}
