package com.example.rewards;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class ShowProfileActivity extends AppCompatActivity {
    private static final Profile profile = new Profile();
    private static String profileInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        profileInfo = intent.getStringExtra("profileInfo");

        loadProfileInfo(profileInfo);
        displayProfileContent();
        displayProfileImage();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.editProfileBtn) {
            Intent intent = new Intent(getApplicationContext(), EditProfileActivity.class);
            intent.putExtra("profileInfo", profileInfo);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayProfileImage() {
        String imageString64 = profile.getImageBytes();
        byte[] decodedString = Base64.decode(imageString64, Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        ImageView profileActImage = findViewById(R.id.profileActImage);
        profileActImage.setImageBitmap(decodedByte);
    }

    private void displayProfileContent() {
        int pointsAwarded = 0;
        int numOfRecords = 0;
        String recordTitle;
        String fullName, userName;

        TextView profileActName = findViewById(R.id.profileActName);
        TextView profileActUserName = findViewById(R.id.profileActUserName);
        TextView profileActLocation = findViewById(R.id.profileActLocation);
        TextView profileActAwarded = findViewById(R.id.profileActAwarded);
        TextView profileActPosition = findViewById(R.id.profileActPosition);
        TextView profileActPointsToAward = findViewById(R.id.profileActPointsToAward);
        TextView profileActStory = findViewById(R.id.profileActStory);
        TextView profileActRewardHistoryTitle = findViewById(R.id.profileActRewardHistoryTitle);

        fullName = profile.getFirstName() + ", " + profile.getLastName();
        profileActName.setText(fullName);
        userName = "(" + profile.getUserName() + ")";
        profileActUserName.setText(userName);
        profileActLocation.setText(profile.getLocation());
        profileActAwarded.setText(String.valueOf(pointsAwarded));
        profileActPosition.setText(profile.getPosition());
        profileActPointsToAward.setText(String.valueOf(profile.getRemainingPointsToAward()));
        profileActStory.setText(profile.getStory());
        if (profile.getRewardRecordViews() != null) numOfRecords = profile.getRewardRecordViews().length();
        recordTitle = "Reward History (" + numOfRecords + ")";
        profileActRewardHistoryTitle.setText(recordTitle);
    }

    private void loadProfileInfo(String profileInfo) {
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setLogo(R.drawable.icon);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        setTitle("Your Profile");
        return super.onCreateOptionsMenu(menu);
    }
}
