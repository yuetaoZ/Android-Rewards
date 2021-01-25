package com.example.rewards;

import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetStudentApiKeyRunnable implements Runnable {

    private final MainActivity mainActivity;

    private static final String dataURL = "http://christopherhield.org/api/";

    private static String firstName, lastName, studentID, email;

    public GetStudentApiKeyRunnable(MainActivity mainActivity, String firstName, String lastName, String studentID, String email) {
        this.mainActivity = mainActivity;
        GetStudentApiKeyRunnable.firstName = firstName;
        GetStudentApiKeyRunnable.lastName = lastName;
        GetStudentApiKeyRunnable.studentID = studentID;
        GetStudentApiKeyRunnable.email = email;
    }

    @Override
    public void run() {
        String urlToUse = generateURL();
        StringBuilder sb = new StringBuilder();

        try {
            URL url = new URL(urlToUse);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            InputStream is = conn.getInputStream();

            BufferedReader reader = new BufferedReader((new InputStreamReader(is)));

            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }

        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        processResults(sb.toString());
    }

    private void processResults(String s) {
        final String apiKey = parseJSON(s);
        if (apiKey != null)  {
            mainActivity.runOnUiThread(() -> mainActivity.updateAPIKey(apiKey));
        }
    }

    private String parseJSON(String s) {
        String apiKey = "some api key";
        JSONObject jObjMain;

        try {
            jObjMain = new JSONObject(s);
            apiKey = jObjMain.getString("apiKey");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return apiKey;
    }

    private String generateURL() {
        String url = dataURL + "Profile/GetStudentApiKey?firstName=" + firstName + "&lastName=" + lastName + "&studentId=" + studentID + "&email=" + email;
        Uri dataUri = Uri.parse(url);
        return dataUri.toString();
    }
}
