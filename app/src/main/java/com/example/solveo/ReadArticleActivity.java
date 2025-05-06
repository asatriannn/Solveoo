package com.example.solveo;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.solveo.Models.ArticleModel;

import java.util.ArrayList;
import java.util.List;

public class ReadArticleActivity extends AppCompatActivity {

    private TextView articleTitle;
    private RecyclerView recyclerView;
    private ArticleModel currentArticle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_article);

        articleTitle = findViewById(R.id.article_title);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        String articleID = getIntent().getStringExtra("ARTICLE_ID");

        for (ArticleModel model : DbQuery.g_articleList) {
            if (model.getArticleID().equals(articleID)) {
                currentArticle = model;
                break;
            }
        }

        if (currentArticle != null) {
            articleTitle.setText(currentArticle.getArticleTitle());

            // Split content into lines or paragraphs
            String[] lines = currentArticle.getArticleContent().split("\\n");

            List<String> paragraphs = new ArrayList<>();
            for (String line : lines) {
                if (!line.trim().isEmpty()) paragraphs.add(line.trim());
            }

            recyclerView.setAdapter(new com.example.solveo.ArticleContentAdapter(paragraphs));
        }
    }
}
