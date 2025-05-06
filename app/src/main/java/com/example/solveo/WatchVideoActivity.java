package com.example.solveo;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.solveo.Models.VideoModel;

public class WatchVideoActivity extends AppCompatActivity {

    private WebView webView;
    private TextView title;
    private TextView description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_video);

        webView = findViewById(R.id.video_player);
        title = findViewById(R.id.video_title);
        description = findViewById(R.id.video_description);

        String videoID = getIntent().getStringExtra("VIDEO_ID");

        for (VideoModel model : DbQuery.g_videoList) {
            if (model.getVideoID().equals(videoID)) {
                title.setText(model.getVideoTitle());
                description.setText(model.getVideoDescription());

                webView.getSettings().setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient());
                webView.loadUrl(model.getVideoLink());
                break;
            }
        }
    }
}
