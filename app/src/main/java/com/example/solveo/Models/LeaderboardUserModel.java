package com.example.solveo.Models;

public class LeaderboardUserModel {
    private String userID;
    private String name;
    private String profileURL;
    private int meanScore;

    public LeaderboardUserModel(String userID, String name, String profileURL, int meanScore) {
        this.userID = userID;
        this.name = name;
        this.profileURL = profileURL;
        this.meanScore = meanScore;
    }

    public String getUserID() {
        return userID;
    }

    public String getName() {
        return name;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public int getMeanScore() {
        return meanScore;
    }
}
