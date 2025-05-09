package com.example.solveo.Models;

public class QuestionModel {

    private String question;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private int selcetedAns;

    private int correctAns;

    private int status;

    public QuestionModel(String question, String optionA, String optionB, String optionC, String optionD, int correctAns, int selcetedAns, int status) {
        this.question = question;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctAns = correctAns;
        this.selcetedAns = selcetedAns;
        this.status = status;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public int getCorrectAns() {
        return correctAns;
    }

    public void setCorrectAns(int correctAns) {
        this.correctAns = correctAns;
    }

    public int getSelcetedAns() {
        return selcetedAns;
    }

    public void setSelcetedAns(int selcetedAns) {
        this.selcetedAns = selcetedAns;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
