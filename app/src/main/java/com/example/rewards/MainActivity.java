package com.example.rewards;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private String APIFirstName;
    private String APILastName;
    private String APIStdID;
    private String APIStdEmail;
    AlertDialog API_dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();

        setupAPI_Dialog();

    }

    private void setupAPI_Dialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.activity_api_key, null);

        final TextView firstName = (EditText) customView.findViewById(R.id.API_firstname);
        final TextView lastName = (EditText) customView.findViewById(R.id.API_lastname);
        final TextView stdEmail = (EditText) customView.findViewById(R.id.API_studentEmail);
        final TextView stdID = (EditText) customView.findViewById(R.id.API_studentID);

        if (APIFirstName != null) firstName.setHint(APIFirstName);
        if (APILastName != null) lastName.setHint(APILastName);
        if (APIStdEmail != null) stdEmail.setHint(APIStdEmail);
        if (APIStdID != null) stdID.setHint(APIStdID);

        API_dialog = new AlertDialog.Builder(this)
                .setView(customView)
                .setIcon(R.drawable.logo)
                .setTitle("API Key Needed")
                .setMessage("You need to request an API Key:")
                .setPositiveButton("OK", (dialog1, whichButton) -> {
                    APIFirstName = firstName.getText().toString();
                    APILastName = lastName.getText().toString();
                    APIStdEmail = stdEmail.getText().toString();
                    APIStdID = stdID.getText().toString();
                    validateInputs();
                })
                .setNegativeButton("Cancel", (dialog12, which) -> {
                    APIFirstName = null;
                    APILastName = null;
                    APIStdEmail = null;
                    APIStdID = null;
                }).create();

        API_dialog.show();
    }

    private void validateInputs() {
        if (APIFirstName.equals("") || APILastName.equals("") || APIStdID.equals("")) {
            showMissingDataDialog();
        } else {
            String email = APIStdEmail.trim();
            String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\." + "edu";
            if (email.matches(emailPattern)) {
                showAPISuccessDialog();
            } else {
                showWrongEmailDialog();
            }
        }
    }

    private void showMissingDataDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Invalid request")
                .setMessage("Missing data")
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", (dialog1, which) -> API_dialog.show())
                .show();
    }

    private void showWrongEmailDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Invalid request")
                .setMessage("Non-edu email address")
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", (dialog1, which) -> API_dialog.show())
                .show();
    }

    private void showAPISuccessDialog() {
        String nameString = APIFirstName.substring(0, 1).toUpperCase() + APIFirstName.substring(1).toLowerCase() + " " +
                APILastName.substring(0, 1).toUpperCase() + APILastName.substring(1).toLowerCase();
        new AlertDialog.Builder(this)
                .setTitle("API Key Received and Stored")
                .setMessage("Name: " + nameString + "\n" +
                            "StudentID: " + APIStdID + "\n" +
                            "Email: " +APIStdEmail + "\n" +
                            "API Key: " + "api key...")
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setupActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }


}