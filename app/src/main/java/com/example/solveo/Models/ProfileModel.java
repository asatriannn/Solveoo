package com.example.solveo.Models;

public class ProfileModel {
    private String Name;
    private String Email_ID;
    private long Mean_Score;

    public ProfileModel() {
        // Required for Firestore deserialization
    }

    public ProfileModel(String name, String email_ID, long mean_Score) {
        Name = name;
        Email_ID = email_ID;
        Mean_Score = mean_Score;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail_ID() {
        return Email_ID;
    }

    public void setEmail_ID(String email_ID) {
        Email_ID = email_ID;
    }

    public long getMean_Score() {
        return Mean_Score;
    }

    public void setMean_Score(long mean_Score) {
        Mean_Score = mean_Score;
    }
}
