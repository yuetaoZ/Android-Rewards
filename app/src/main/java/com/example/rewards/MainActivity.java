package com.example.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private String APIfirstName;
    private String APILastName;
    private String APIStdID;
    private String APIStdEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();

        if (!validAPI()) showAPIDialog();

    }

    private boolean validAPI() {
        return false;
    }

    private void showAPIDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.activity_api_key, null);

        final TextView firstName = (EditText) customView.findViewById(R.id.API_firstname);
        final TextView lastName = (EditText) customView.findViewById(R.id.API_lastname);
        final TextView stdEmail = (EditText) customView.findViewById(R.id.API_studentEmail);
        final TextView stdID = (EditText) customView.findViewById(R.id.API_studentID);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(customView)
                .setIcon(R.drawable.logo)
                .setTitle("API Key Needed")
                .setMessage("You need to request an API Key:")
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    APIfirstName = firstName.getText().toString();
                    APILastName = lastName.getText().toString();
                    APIStdEmail = stdEmail.getText().toString();
                    APIStdID = stdID.getText().toString();
                })
                .setNegativeButton("Cancel", (dialog12, which) -> {
                    APIfirstName = null;
                    APILastName = null;
                    APIStdEmail = null;
                    APIStdID = null;
                }).create();

        if (APIfirstName != null) firstName.setHint(APIfirstName);
        if (APILastName != null) lastName.setHint(APILastName);
        if (APIStdEmail != null) stdEmail.setHint(APIStdEmail);
        if (APIStdID != null) stdID.setHint(APIStdID);
        dialog.show();
    }

    private void setupActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }


}