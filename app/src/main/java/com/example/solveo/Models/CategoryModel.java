package com.example.solveo.Models;

public class CategoryModel {

    private String docID;
    private String name;
    private int numberOfTests;

    // Constructor including numberOfTests
    public CategoryModel(String docID, String name, int numberOfTests) {
        this.docID = docID;
        this.name = name;
        this.numberOfTests = numberOfTests;
    }

    public String getDocID() {
        return docID;
    }

    public void setDocID(String docID) {
        this.docID = docID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfTests() {
        return numberOfTests;
    }

    public void setNumberOfTests(int numberOfTests) {
        this.numberOfTests = numberOfTests;
    }
}
