package com.example.rewards;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    public static final String EXTRA_APIKEY = "";
    private String APIFirstName = "";
    private String APILastName = "";
    private String APIStdID = "";
    private String APIStdEmail = "";
    private String APIKey = "";
    private boolean APIKeySaved = false;
    private AlertDialog API_dialog;

    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String FIRSTNAME = "firstName";
    private static final String LASTNAME = "lastName";
    private static final String STUDENT_EMAIL = "studentEmail";
    private static final String STUDENT_ID = "studentID";
    private static final String APIKEY = "studentAPIKey";
    private static final String APIKEY_SAVED = "APIKEYSaved";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupActionBar();
        setupClearAPIKeyButton();
        setupCreateProfileButton();
        setupAPI_Dialog();
        setupLoginButton();
        setupRememberCredentialCheckBox();
        setupTextWatcherForEditTexts();
    }

    private void setupCreateProfileButton() {
        Button createProfileButton = findViewById(R.id.createProfileButton);
        createProfileButton.setOnClickListener(v -> {
            loadData();
            if (APIKey.equals("")) {
                setupAPI_Dialog();
            } else {
                Intent intent = new Intent(MainActivity.this, CreateProfileActivity.class);
                intent.putExtra(EXTRA_APIKEY, APIKey);
                startActivity(intent);
            }
        });
    }


    private void setupLoginButton() {
        Button loginButton = findViewById(R.id.loginButton);
        loginButton.setOnClickListener(v -> {
            EditText textUsername = findViewById(R.id.editTextUsername);
            String username = textUsername.getText().toString();
            EditText textPassword = findViewById(R.id.editTextPassword);
            String password = textPassword.getText().toString();

            Profile profile = new Profile();
            profile.setUserName(username);
            profile.setPassword(password);

            LoginAPIRunnable loginAPIRunnable = new LoginAPIRunnable(this, profile, APIKey);
            new Thread(loginAPIRunnable).start();
        });
    }


    private void setupRememberCredentialCheckBox() {
        CheckBox chk = findViewById(R.id.rememberCredentialsCheckBox);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        boolean checkedState = sharedPreferences.getBoolean("checkedState", false);
        chk.setChecked(checkedState);

        EditText textUsername = findViewById(R.id.editTextUsername);
        EditText textPassword = findViewById(R.id.editTextPassword);

        if (checkedState) {
            textUsername.setText(sharedPreferences.getString(USERNAME, ""));
            textPassword.setText(sharedPreferences.getString(PASSWORD, ""));
        } else {
            textUsername.setText("");
            textPassword.setText("");
        }

        chk.setOnClickListener(v -> {
            boolean checked = ((CheckBox) v).isChecked();
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("checkedState", checked);
            editor.apply();
        });
    }

    private void setupTextWatcherForEditTexts() {
        final EditText editUsername = findViewById(R.id.editTextUsername);
        editUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(USERNAME, editUsername.getText().toString());
                editor.apply();
            }
        });
        final EditText editPassword = findViewById(R.id.editTextPassword);
        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PASSWORD, editPassword.getText().toString());
                editor.apply();
            }
        });
    }


    private void setupClearAPIKeyButton() {
        Button clearAPIKeyBtn = findViewById(R.id.clearAPIKeyButton);
        clearAPIKeyBtn.setOnClickListener(v -> {
            clearSavedAPI();
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("API Key Cleared")
                    .setMessage("API Key cleared successfully!")
                    .setIcon(R.drawable.logo)
                    .setPositiveButton("OK", (dialog, which) -> setupAPI_Dialog())
                    .show();
        });
    }

    private void setupAPI_Dialog() {
        loadData();
        if (APIKeySaved) return;
        LayoutInflater inflater = LayoutInflater.from(this);
        final View customView = inflater.inflate(R.layout.activity_api_key, null);

        final TextView firstName = customView.findViewById(R.id.API_firstname);
        final TextView lastName = customView.findViewById(R.id.API_lastname);
        final TextView stdEmail = customView.findViewById(R.id.API_studentEmail);
        final TextView stdID = customView.findViewById(R.id.API_studentID);

        if (!APIFirstName.equals("")) firstName.setHint(APIFirstName);
        if (!APILastName.equals("")) lastName.setHint(APILastName);
        if (!APIStdEmail.equals("")) stdEmail.setHint(APIStdEmail);
        if (!APIStdID.equals("")) stdID.setHint(APIStdID);

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
                    APIFirstName = "";
                    APILastName = "";
                    APIStdEmail = "";
                    APIStdID = "";
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
                requestAPIKey();
            } else {
                showWrongEmailDialog();
            }
        }
    }

    private void requestAPIKey() {
        GetStudentApiKeyRunnable alr = new GetStudentApiKeyRunnable(this, APIFirstName, APILastName, APIStdID, APIStdEmail);
        new Thread(alr).start();
    }

    private void showMissingDataDialog() {
        String message = "";
        if (APIFirstName.equals("")) message += "Missing data: First Name.\n";
        if (APILastName.equals("")) message += "Missing data: Last Name.\n";
        if (APIStdEmail.equals("")) message += "Missing data: Student Email.\n";
        if (APIStdID.equals("")) message += "Missing data: Student ID.\n";
        new AlertDialog.Builder(this)
                .setTitle("Invalid request")
                .setMessage(message)
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", (dialog1, which) -> API_dialog.show())
                .show();
    }

    private void showWrongEmailDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Invalid email address")
                .setMessage("Not a valid edu email address.")
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
                            "Email: " + APIStdEmail + "\n" +
                            "API Key: " + APIKey)
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", null)
                .show();
    }

    private void setupActionBar() {
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
    }

    public void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(FIRSTNAME, APIFirstName);
        editor.putString(LASTNAME, APILastName);
        editor.putString(STUDENT_EMAIL, APIStdEmail);
        editor.putString(STUDENT_ID, APIStdID);
        editor.putString(APIKEY, APIKey);
        editor.putBoolean(APIKEY_SAVED, !APIFirstName.equals(""));

        editor.apply();
    }

    public void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        APIFirstName = sharedPreferences.getString(FIRSTNAME, "");
        APILastName = sharedPreferences.getString(LASTNAME, "");
        APIStdEmail = sharedPreferences.getString(STUDENT_EMAIL, "");
        APIStdID = sharedPreferences.getString(STUDENT_ID, "");
        APIKey = sharedPreferences.getString(APIKEY, "");
        APIKeySaved = sharedPreferences.getBoolean(APIKEY_SAVED, false);
    }

    public void clearSavedAPI() {
        APIFirstName = "";
        APILastName = "";
        APIStdEmail = "";
        APIStdID = "";
        APIKey = "";
        saveData();
    }

    public void updateAPIKey(String apiKey) {
        APIKey = apiKey;
        saveData();
        showAPISuccessDialog();
    }

    public void showLoginError(String s) {
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("Login Failed")
                .setMessage(s)
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", null)
                .show());
    }
}