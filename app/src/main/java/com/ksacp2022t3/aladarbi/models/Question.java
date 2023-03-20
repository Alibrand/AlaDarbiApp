package com.ksacp2022t3.aladarbi.models;

public class Question {
    String text;
    String answer;

    public Question(String text, String answer) {
        this.text = text;
        this.answer = answer;
    }

    public String getText() {
        return text;
    }

    public String getAnswer() {
        return answer;
    }
}
