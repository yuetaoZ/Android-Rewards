package com.example.rewards;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginAPIRunnable implements Runnable {
    private static final String TAG = "myApp";
    private final MainActivity mainActivity;
    private final Profile profile;
    private final String apiKey;
    private static final String baseURL = "http://www.christopherhield.org/api/";
    private static final String endPoint = "Profile/Login";

    LoginAPIRunnable(MainActivity mainActivity, Profile profile, String apiKey) {
        this.mainActivity = mainActivity;
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

            buildURL.appendQueryParameter("userName", profile.getUserName());
            buildURL.appendQueryParameter("password", profile.getPassword());

            String urlToUse = buildURL.build().toString();
            URL url = new URL(urlToUse);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("ApiKey", apiKey);
            connection.connect();

            int responseCode = connection.getResponseCode();
            StringBuilder result = new StringBuilder();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                loadProfile(result.toString());
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));

                String line;
                while (null != (line = reader.readLine())) {
                    result.append(line).append("\n");
                }
                mainActivity.showLoginError(result.toString());
            }
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
    }

    private void loadProfile(String s) {
        Intent intent = new Intent(mainActivity.getApplicationContext(), ShowProfileActivity.class);
        intent.putExtra("profileInfo", s);
        mainActivity.startActivity(intent);
    }
}
