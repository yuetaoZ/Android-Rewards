package com.example.rewards;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_FROM_GALLERY = 1;
    private static final Profile profile = new Profile();
    private static final String SHARED_PREFS = "sharedPrefs";
    private static String apiKey;

    private TextView editProfileUsername, editStoryTitle;
    private EditText editProfilePassword, editProfileFirstName, editProfileLastName,
            editProfileDepartment, editPositionTitle, editStoryContent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        String profileInfo = intent.getStringExtra("profileInfo");

        initialFields();
        Profile profile = createProfile(profileInfo);
        loadProfile(profile);
        setupTextWatcherForStory();
        setupClickForImageView();
    }

    private void initialFields() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        apiKey = sharedPreferences.getString("studentAPIKey", "");
        editProfileUsername = findViewById(R.id.editProfileUsername);
        editProfilePassword = findViewById(R.id.editProfilePassword);
        editProfileFirstName = findViewById(R.id.editProfileFirstName);
        editProfileLastName = findViewById(R.id.editProfileLastName);
        editProfileDepartment = findViewById(R.id.editProfileDepartment);
        editPositionTitle = findViewById(R.id.editPositionTitle);
        editStoryTitle = findViewById(R.id.editStoryTitle);
        editStoryContent = findViewById(R.id.editStoryContent);
    }

    private Profile createProfile(String profileInfo) {
        JSONObject jObjMain;

        try {
            jObjMain = new JSONObject(profileInfo);
            profile.setFirstName(jObjMain.getString("firstName"));
            profile.setLastName(jObjMain.getString("lastName"));
            profile.setUserName(jObjMain.getString("userName"));
            profile.setDepartment(jObjMain.getString("department"));
            profile.setStory(jObjMain.getString("story"));
            profile.setPosition(jObjMain.getString("position"));
            profile.setPassword(jObjMain.getString("password"));
            profile.setRemainingPointsToAward(jObjMain.getInt("remainingPointsToAward"));
            profile.setLocation(jObjMain.getString("location"));
            profile.setImageBytes(jObjMain.getString("imageBytes"));
            profile.setRewardRecordViews(jObjMain.getJSONArray("rewardRecordViews"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return profile;
    }

    private void setupClickForImageView() {
        ImageView ChoosePhoto = findViewById(R.id.editProfileImage);
        ChoosePhoto.setOnClickListener(v -> {
            try {
                if (ActivityCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(EditProfileActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, PICK_FROM_GALLERY);
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

    private void setupTextWatcherForStory() {
        EditText story = findViewById(R.id.editStoryContent);
        TextView storyTitle = findViewById(R.id.editStoryTitle);
        final String[] title = new String[1];
        story.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                title[0] = "Your Story: (" + s.length() + " of 360)";
                storyTitle.setText(title[0]);
            }
        });
    }

    private void loadProfile(Profile profile) {
        loadTexts(profile);
        loadImage(profile);
    }

    private void loadImage(Profile profile) {
        String imageString64 = profile.getImageBytes();
        byte[] decodedString = Base64.decode(imageString64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        ImageView profileActImage = findViewById(R.id.editProfileImage);
        profileActImage.setImageBitmap(decodedByte);
    }

    private void loadTexts(Profile profile) {
        String storyTitle, story;

        editProfileUsername.setText(profile.getUserName());
        editProfilePassword.setText(profile.getPassword());
        editProfileFirstName.setText(profile.getFirstName());
        editProfileLastName.setText(profile.getLastName());
        editProfileDepartment.setText(profile.getDepartment());
        editPositionTitle.setText(profile.getPosition());
        story = profile.getStory();
        storyTitle = "Your Story: (" + story.length() + " of 360)";
        editStoryTitle.setText(storyTitle);
        editStoryContent.setText(story);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_save, menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setTitle("Edit Profile");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.saveButton) {
            runOnUiThread(() -> new AlertDialog.Builder(this)
                    .setTitle("Save Changes?")
                    .setIcon(R.drawable.logo)
                    .setPositiveButton("OK", (dialog, which) -> updateProfile())
                    .setNegativeButton("CANCEL", null)
                    .show());
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {
        if (validateInputs()) {
            saveChanges();
            new Thread(new UpdateProfileAPIRunnable(this, profile, apiKey)).start();
        }
    }

    private void saveChanges() {
        profile.setPassword(editProfilePassword.getText().toString());
        profile.setFirstName(editProfileFirstName.getText().toString());
        profile.setLastName(editProfileLastName.getText().toString());
        profile.setDepartment(editProfileDepartment.getText().toString());
        profile.setStory(editStoryContent.getText().toString());
    }

    public void showResults(String s) {
        if (!s.equals("Error performing POST request")) {
            runOnUiThread(() -> new AlertDialog.Builder(this)
                    .setTitle("Update Profile")
                    .setMessage("Profile update succeed.")
                    .setIcon(R.drawable.logo)
                    .setPositiveButton("OK", (dialog1, which) -> updateProfile(s))
                    .show());
        } else {
            runOnUiThread(() -> new AlertDialog.Builder(this)
                    .setTitle("Update Profile")
                    .setMessage(s)
                    .setIcon(R.drawable.logo)
                    .setPositiveButton("OK", null)
                    .show());
        }
    }

    private void updateProfile(String s) {
        Intent intent = new Intent(this, ShowProfileActivity.class);
        intent.putExtra("profileInfo", s);
        startActivity(intent);
    }

    private boolean validateInputs() {
        boolean checkUsername, checkPassword, checkFirstname, checkLastname, checkDepartment,
                checkPosition, checkStoryContent;
        String strEditProfileUsername = editProfileUsername.getText().toString();

        if (TextUtils.isEmpty(strEditProfileUsername)) {
            editProfileUsername.setError("This item can not be empty.");
            checkUsername = false;
        } else if (strEditProfileUsername.length() > 20) {
            editProfileUsername.setError("This item can not be longer than 20 character.");
            checkUsername = false;
        } else {
            checkUsername = true;
        }

        String strEditProfilePassword = editProfilePassword.getText().toString();

        if (TextUtils.isEmpty(strEditProfilePassword)) {
            editProfilePassword.setError("This item can not be empty.");
            checkPassword = false;
        } else if (strEditProfilePassword.length() > 40) {
            editProfilePassword.setError("This item can not be longer than 40 character.");
            checkPassword = false;
        } else {
            checkPassword = true;
        }

        String strEditProfileFirstName = editProfileFirstName.getText().toString();

        if (TextUtils.isEmpty(strEditProfileFirstName)) {
            editProfileFirstName.setError("This item can not be empty.");
            checkFirstname = false;
        } else if (strEditProfileFirstName.length() > 20) {
            editProfileFirstName.setError("This item can not be longer than 20 character.");
            checkFirstname = false;
        } else {
            checkFirstname = true;
        }

        String strEditProfileLastName = editProfileLastName.getText().toString();

        if (TextUtils.isEmpty(strEditProfileLastName)) {
            editProfileLastName.setError("This item can not be empty.");
            checkLastname = false;
        } else if (strEditProfileLastName.length() > 20) {
            editProfileLastName.setError("This item can not be longer than 20 character.");
            checkLastname = false;
        } else {
            checkLastname = true;
        }

        String strEditProfileDepartment = editProfileDepartment.getText().toString();

        if (TextUtils.isEmpty(strEditProfileDepartment)) {
            editProfileDepartment.setError("This item can not be empty.");
            checkDepartment = false;
        } else if (strEditProfileDepartment.length() > 30) {
            editProfileDepartment.setError("This item can not be longer than 30 character.");
            checkDepartment = false;
        } else {
            checkDepartment = true;
        }

        String strEditPositionTitle = editPositionTitle.getText().toString();

        if (TextUtils.isEmpty(strEditPositionTitle)) {
            editPositionTitle.setError("This item can not be empty.");
            checkPosition = false;
        } else if (strEditPositionTitle.length() > 20) {
            editPositionTitle.setError("This item can not be longer than 20 character.");
            checkPosition = false;
        } else {
            checkPosition = true;
        }


        String strEditStoryContent = editStoryContent.getText().toString();

        if (TextUtils.isEmpty(strEditStoryContent)) {
            editStoryContent.setError("This item can not be empty.");
            checkStoryContent = false;
        } else if (strEditStoryContent.length() > 360) {
            editStoryContent.setError("This item can not be longer than 360 character.");
            checkStoryContent = false;
        } else {
            checkStoryContent = true;
        }

        return checkUsername && checkPassword && checkFirstname && checkLastname && checkDepartment &&
                checkPosition && checkStoryContent;
    }

}
