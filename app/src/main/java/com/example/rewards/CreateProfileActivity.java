package com.example.rewards;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

public class CreateProfileActivity extends AppCompatActivity {
    private static final String TAG = "myApp Rewards";
    private static final int PICK_FROM_GALLERY = 1;
    private static Bitmap bitmap;
    private static String apiKey, strSelectUserName, strSelectUPassword, strProfileFirstName,
            strProfileLastName, strDepartmentName, strPositionTitle, strStoryContent;
    private static final String locationString = "Unspecified Location";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_profile);

        Intent intent = getIntent();
        apiKey = intent.getStringExtra(MainActivity.EXTRA_APIKEY);

        setupClickForImageView();

    }

    private void setupClickForImageView() {

        ImageView ChoosePhoto = findViewById(R.id.profileImage);
        ChoosePhoto.setOnClickListener(v -> {
            try {
                if (ActivityCompat.checkSelfPermission(CreateProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
                } else {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_GALLERY);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        Log.d(TAG, "Got permission from request with requestCode:" + requestCode);
        if (requestCode == PICK_FROM_GALLERY) {// If request is cancelled, the result arrays are empty.
            Log.d(TAG, "Got requestResult: " + grantResults[0]);
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Got permission from request.");
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_FROM_GALLERY);
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == PICK_FROM_GALLERY) {
                if (resultCode == Activity.RESULT_OK) {
                    Uri imageUri = data.getData();
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ImageView profileImage = findViewById(R.id.profileImage);
                    int wid = profileImage.getWidth();
                    int hit = profileImage.getHeight();
                    bitmap = Bitmap.createScaledBitmap(bitmap, wid, hit, false);
                    profileImage.setImageBitmap(bitmap);
                    Log.d(TAG, "setImage");
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
        if (validateInputs()) {
            Profile profile = createProfile();
            new Thread(new CreateProfileAPIRunnable(this, profile, apiKey)).start();
        }
    }

    public void showResults(final String s) {
        String message;
        if (s != null && s.length() > 100) {
            message = s.substring(0, 300) + "...";
        } else {
            message = s;
        }
        Log.i(TAG, "showResults called");
        runOnUiThread(() -> new AlertDialog.Builder(this)
                .setTitle("Create Profile")
                .setMessage(message)
                .setIcon(R.drawable.logo)
                .setPositiveButton("OK", null)
                .show());
    }

    private Profile createProfile() {
        Profile profile = new Profile();
        profile.setUserName(strSelectUserName);
        profile.setPassword(strSelectUPassword);
        profile.setFirstName(strProfileFirstName);
        profile.setLastName(strProfileLastName);
        profile.setDepartment(strDepartmentName);
        profile.setPosition(strPositionTitle);
        profile.setStory(strStoryContent);
        if (bitmap == null) setDefaultPicture();
        profile.setImageBytes(compressToBase64(bitmap));
        profile.setLocation(locationString);
        Log.i(TAG, "createProfile");
        return profile;
    }

    private void setDefaultPicture() {
        ImageView profileImage = findViewById(R.id.profileImage);
        profileImage.invalidate();
        BitmapDrawable drawable = (BitmapDrawable) profileImage.getDrawable();
        bitmap = drawable.getBitmap();
    }

    private String compressToBase64(Bitmap bitmap) {
        String imageString64;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 80, baos);
        byte[] byteArray = baos.toByteArray();
        imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);

        while (imageString64.length() > 100000) {
            int qualityRate = 1/(imageString64.length() / 100000) * 90;
            bitmap.compress(Bitmap.CompressFormat.PNG, qualityRate, baos);
            byteArray = baos.toByteArray();
            imageString64 = Base64.encodeToString(byteArray, Base64.DEFAULT);
        }

        return imageString64;
    }


    private boolean validateInputs() {
        boolean checkUsername, checkPassword, checkFirstname, checkLastname, checkDepartment,
                checkPosition, checkStoryContent;
        EditText selectUserName = findViewById(R.id.selectUsername);
        strSelectUserName = selectUserName.getText().toString();

        if (TextUtils.isEmpty(strSelectUserName)) {
            selectUserName.setError("This item can not be empty.");
            checkUsername = false;
        } else if (strSelectUserName.length() > 20) {
            selectUserName.setError("This item can not be longer than 20 character.");
            checkUsername = false;
        } else {
            checkUsername = true;
        }

        EditText selectUPassword = findViewById(R.id.selectPassword);
        strSelectUPassword = selectUPassword.getText().toString();

        if (TextUtils.isEmpty(strSelectUPassword)) {
            selectUPassword.setError("This item can not be empty.");
            checkPassword = false;
        } else if (strSelectUPassword.length() > 40) {
            selectUPassword.setError("This item can not be longer than 40 character.");
            checkPassword = false;
        } else {
            checkPassword = true;
        }

        EditText profileFirstName = findViewById(R.id.profileFirstName);
        strProfileFirstName = profileFirstName.getText().toString();

        if (TextUtils.isEmpty(strProfileFirstName)) {
            profileFirstName.setError("This item can not be empty.");
            checkFirstname = false;
        } else if (strProfileFirstName.length() > 20) {
            profileFirstName.setError("This item can not be longer than 20 character.");
            checkFirstname = false;
        } else {
            checkFirstname = true;
        }

        EditText profileLastName = findViewById(R.id.profileLastName);
        strProfileLastName = profileLastName.getText().toString();

        if (TextUtils.isEmpty(strProfileLastName)) {
            profileLastName.setError("This item can not be empty.");
            checkLastname = false;
        } else if (strProfileLastName.length() > 20) {
            profileLastName.setError("This item can not be longer than 20 character.");
            checkLastname = false;
        } else {
            checkLastname = true;
        }

        EditText departmentName = findViewById(R.id.departmentName);
        strDepartmentName = departmentName.getText().toString();

        if (TextUtils.isEmpty(strDepartmentName)) {
            departmentName.setError("This item can not be empty.");
            checkDepartment = false;
        } else if (strDepartmentName.length() > 30) {
            departmentName.setError("This item can not be longer than 30 character.");
            checkDepartment = false;
        } else {
            checkDepartment = true;
        }

        EditText positionTitle = findViewById(R.id.positionTitle);
        strPositionTitle = positionTitle.getText().toString();

        if (TextUtils.isEmpty(strPositionTitle)) {
            positionTitle.setError("This item can not be empty.");
            checkPosition = false;
        } else if (strPositionTitle.length() > 20) {
            positionTitle.setError("This item can not be longer than 20 character.");
            checkPosition = false;
        } else {
            checkPosition = true;
        }

        EditText storyContent = findViewById(R.id.storyContent);
        strStoryContent = storyContent.getText().toString();

        if (TextUtils.isEmpty(strStoryContent)) {
            storyContent.setError("This item can not be empty.");
            checkStoryContent = false;
        } else if (strPositionTitle.length() > 360) {
            storyContent.setError("This item can not be longer than 360 character.");
            checkStoryContent = false;
        } else {
            checkStoryContent = true;
        }

        return checkUsername && checkPassword && checkFirstname && checkLastname && checkDepartment &&
                checkPosition && checkStoryContent;
    }

}
