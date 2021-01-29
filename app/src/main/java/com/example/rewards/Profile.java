package com.example.rewards;

import org.json.JSONArray;


public class Profile {
    private String firstName;
    private String lastName;
    private String userName;
    private String department;
    private String story;
    private String position;
    private String password;
    private String location;
    private String imageBytes;
    private int remainingPointsToAward = 1000;
    private JSONArray rewardRecordViews;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getRemainingPointsToAward() {
        return remainingPointsToAward;
    }

    public void setRemainingPointsToAward(int remainingPointsToAward) {
        this.remainingPointsToAward = remainingPointsToAward;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageBytes() {
        return imageBytes;
    }

    public void setImageBytes(String imageBytes) {
        this.imageBytes = imageBytes;
    }

    public JSONArray getRewardRecordViews() {
        return rewardRecordViews;
    }

    public void setRewardRecordViews(JSONArray rewardRecordViews) {
        this.rewardRecordViews = rewardRecordViews;
    }
}
