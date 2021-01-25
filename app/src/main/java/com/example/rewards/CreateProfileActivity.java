package com.example.rewards;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

public class CreateProfileActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final String TAG = "myApp Rewards";
    private Bitmap bitmap;
    private static String locationString = "Unspecified Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Intent intent = getIntent();
        String apiKey = intent.getStringExtra(MainActivity.EXTRA_APIKEY);

        setupClickForImageView();

    }

    private void setupClickForImageView() {
        ImageView profileImage = findViewById(R.id.profileImage);
        profileImage.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == REQUEST_CODE) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ImageView profileImage = findViewById(R.id.profileImage);
                    int wid = profileImage.getWidth();
                    int hit = profileImage.getHeight();
                    bitmap = Bitmap.createScaledBitmap(bitmap, wid, hit, false);
                    profileImage.setImageBitmap(bitmap);
                    Log.w(TAG, "setImage");
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    Log.e(TAG, "Selecting picture cancelled");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in onActivityResult : " + e.getMessage());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_create_profile, menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.saveButton) {
            saveProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveProfile() {
        validateInputs();
    }

    private void validateInputs() {
        EditText selectUserName = findViewById(R.id.selectUsername);
        String strSelectUserName = selectUserName.getText().toString();

        if (TextUtils.isEmpty(strSelectUserName)) {
            selectUserName.setError("This item can not be empty.");
        } else if (strSelectUserName.length() > 20) {
            selectUserName.setError("This item can not be longer than 20 character.");
        }

        EditText selectUPassword = findViewById(R.id.selectPassword);
        String strSelectUPassword = selectUPassword.getText().toString();

        if (TextUtils.isEmpty(strSelectUPassword)) {
            selectUPassword.setError("This item can not be empty.");
        } else if (strSelectUPassword.length() > 40) {
            selectUPassword.setError("This item can not be longer than 40 character.");
        }

        EditText profileFirstName = findViewById(R.id.profileFirstName);
        String strProfileFirstName = profileFirstName.getText().toString();

        if (TextUtils.isEmpty(strProfileFirstName)) {
            profileFirstName.setError("This item can not be empty.");
        } else if (strProfileFirstName.length() > 20) {
            profileFirstName.setError("This item can not be longer than 20 character.");
        }

        EditText profileLastName = findViewById(R.id.profileLastName);
        String strProfileLastName = profileLastName.getText().toString();

        if (TextUtils.isEmpty(strProfileLastName)) {
            profileLastName.setError("This item can not be empty.");
        } else if (strProfileLastName.length() > 20) {
            profileLastName.setError("This item can not be longer than 20 character.");
        }

        EditText departmentName = findViewById(R.id.departmentName);
        String strDepartmentName = departmentName.getText().toString();

        if (TextUtils.isEmpty(strDepartmentName)) {
            departmentName.setError("This item can not be empty.");
        } else if (strDepartmentName.length() > 30) {
            departmentName.setError("This item can not be longer than 30 character.");
        }

        EditText positionTitle = findViewById(R.id.positionTitle);
        String strPositionTitle = positionTitle.getText().toString();

        if (TextUtils.isEmpty(strPositionTitle)) {
            positionTitle.setError("This item can not be empty.");
        } else if (strPositionTitle.length() > 20) {
            positionTitle.setError("This item can not be longer than 20 character.");
        }

    }
}
