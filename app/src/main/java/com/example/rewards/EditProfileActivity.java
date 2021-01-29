package com.example.rewards;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class EditProfileActivity extends AppCompatActivity {
    private static final int PICK_FROM_GALLERY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        String profileInfo = intent.getStringExtra("profileInfo");

        Profile profile = createProfile(profileInfo);
        loadProfile(profile);
        setupTextWatcherForStory();
        setupClickForImageView();
    }

    private Profile createProfile(String profileInfo) {
        Profile profile = new Profile();
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

        TextView editProfileUsername = findViewById(R.id.editProfileUsername);
        EditText editProfilePassword = findViewById(R.id.editProfilePassword);
        EditText editProfileFirstName = findViewById(R.id.editProfileFirstName);
        EditText editProfileLastName = findViewById(R.id.editProfileLastName);
        EditText editProfileDepartment = findViewById(R.id.editProfileDepartment);
        EditText editPositionTitle = findViewById(R.id.editPositionTitle);
        TextView editStoryTitle = findViewById(R.id.editStoryTitle);
        EditText editStoryContent = findViewById(R.id.editStoryContent);

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
            updateProfile();
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {
    }

}
