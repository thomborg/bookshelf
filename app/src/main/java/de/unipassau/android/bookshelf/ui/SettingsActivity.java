package de.unipassau.android.bookshelf.ui;

import android.os.Bundle;
import android.os.PersistableBundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import de.unipassau.android.bookshelf.R;

public class SettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_settings);

       // getSupportFragmentManager().beginTransaction().replace(R.id.settingFrag, new SettingsFragment()).commit();
    }
}
