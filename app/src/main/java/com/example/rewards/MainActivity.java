package com.example.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();
        checkAPIInfo(savedInstanceState);
    }

    private void checkAPIInfo(Bundle savedInstanceState) {
        APIInfoDialogFragment apiDialogFragment = new APIInfoDialogFragment();
        apiDialogFragment.show(getSupportFragmentManager(), "apikey");
    }

    private void setupActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }


}