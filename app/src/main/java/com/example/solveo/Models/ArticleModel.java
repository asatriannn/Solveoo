package com.example.solveo.Models;

public class ArticleModel {

    private String articleID;
    private String articleTitle;
    private String articleContent;

    public ArticleModel(String articleContent, String articleTitle, String articleID) {
        this.articleContent = articleContent;
        this.articleTitle = articleTitle;
        this.articleID = articleID;
    }

    public ArticleModel() {
    }

    public String getArticleContent() {
        return articleContent;
    }

    public void setArticleContent(String articleContent) {
        this.articleContent = articleContent;
    }

    public String getArticleID() {
        return articleID;
    }

    public void setArticleID(String articleID) {
        this.articleID = articleID;
    }

    public String getArticleTitle() {
        return articleTitle;
    }

    public void setArticleTitle(String articleTitle) {
        this.articleTitle = articleTitle;
    }
}
