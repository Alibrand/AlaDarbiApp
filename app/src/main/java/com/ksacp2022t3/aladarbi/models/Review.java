package com.ksacp2022t3.aladarbi.models;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Review {
    String name;
    String comment;
    double rate;
    @ServerTimestamp
    Date created_at;

    public Review() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public Date getCreated_at() {
        return created_at;
    }

    public void setCreated_at(Date created_at) {
        this.created_at = created_at;
    }
}
