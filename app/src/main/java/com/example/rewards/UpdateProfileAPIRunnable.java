package com.example.rewards;

import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class UpdateProfileAPIRunnable implements Runnable {
    private static final String TAG = "myApp";
    private final Profile profile;
    private final String apiKey;
    private final EditProfileActivity editProfileActivity;
    private static final String baseURL = "http://www.christopherhield.org/api";
    private static final String endPoint = "/Profile/UpdateProfile";

    public UpdateProfileAPIRunnable(EditProfileActivity editProfileActivity, Profile profile, String apiKey) {
        this.editProfileActivity = editProfileActivity;
        this.profile = profile;
        this.apiKey = apiKey;
    }

    @Override
    public void run() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            String urlString = baseURL + endPoint;
            Uri.Builder buildURL = Uri.parse(urlString).buildUpon();

            Log.d(TAG, "run: Initial URL: " + urlString);

            buildURL.appendQueryParameter("firstName", profile.getFirstName());
            buildURL.appendQueryParameter("lastName", profile.getLastName());
            buildURL.appendQueryParameter("userName", profile.getUserName());
            buildURL.appendQueryParameter("department", profile.getDepartment());
            buildURL.appendQueryParameter("story", profile.getStory());
            buildURL.appendQueryParameter("position", profile.getPosition());
            buildURL.appendQueryParameter("password", profile.getPassword());
            buildURL.appendQueryParameter("remainingPointsToAward", String.valueOf(profile.getRemainingPointsToAward()));
            buildURL.appendQueryParameter("location", profile.getLocation());

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
            out.write(profile.getImageBytes());
            out.close();

            int responseCode = connection.getResponseCode();
            StringBuilder result = new StringBuilder();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
            }

            editProfileActivity.showResults(result.toString());
            return;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "doInBackground: Error closing stream: " + e.getMessage());
                }
            }
        }
        editProfileActivity.showResults("Error performing POST request");
    }
}
