package com.example.solveo.Models;

public class VideoModel {
    private String videoID, videoTitle, videoDescription, videoLink;

    public VideoModel(String videoID, String videoTitle, String videoDescription, String videoLink) {
        this.videoID = videoID;
        this.videoTitle = videoTitle;
        this.videoDescription = videoDescription;
        this.videoLink = videoLink;
    }

    public String getVideoID() { return videoID; }
    public String getVideoTitle() { return videoTitle; }
    public String getVideoDescription() { return videoDescription; }
    public String getVideoLink() { return videoLink; }
}
