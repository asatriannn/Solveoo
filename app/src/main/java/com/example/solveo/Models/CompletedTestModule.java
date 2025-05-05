package com.example.solveo.Models;

public class CompletedTestModule {
    private String tetsID;
    private int score;

    public CompletedTestModule(String tetsID, int score) {
        this.tetsID = tetsID;
        this.score = score;
    }

    public String getTetsID() {
        return tetsID;
    }

    public void setTetsID(String tetsID) {
        this.tetsID = tetsID;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
