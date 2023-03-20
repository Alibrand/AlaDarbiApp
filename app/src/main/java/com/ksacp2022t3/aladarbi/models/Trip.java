package com.ksacp2022t3.aladarbi.models;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Trip {
    String id;
    String driver_id;
    String driver_name;
    String car_model;
    String car_number;
    int max_passengers_count;
    GeoPoint start_point;
    GeoPoint end_point;
    double total_distance;
    double total_price;
    @ServerTimestamp
    Date crated_at;
    Date trip_date;
    String trip_type="يومياً";
    int departure_hours;
    int departure_minutes;

    public Trip() {
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCar_model() {
        return car_model;
    }

    public void setCar_model(String car_model) {
        this.car_model = car_model;
    }

    public String getCar_number() {
        return car_number;
    }

    public void setCar_number(String car_number) {
        this.car_number = car_number;
    }

    public int getMax_passengers_count() {
        return max_passengers_count;
    }

    public void setMax_passengers_count(int max_passengers_count) {
        this.max_passengers_count = max_passengers_count;
    }

    public GeoPoint getStart_point() {
        return start_point;
    }

    public void setStart_point(GeoPoint start_point) {
        this.start_point = start_point;
    }

    public GeoPoint getEnd_point() {
        return end_point;
    }

    public void setEnd_point(GeoPoint end_point) {
        this.end_point = end_point;
    }

    public double getTotal_distance() {
        return total_distance;
    }

    public void setTotal_distance(double total_distance) {
        this.total_distance = total_distance;
    }

    public double getTotal_price() {
        return total_price;
    }

    public void setTotal_price(double total_price) {
        this.total_price = total_price;
    }

    public Date getCrated_at() {
        return crated_at;
    }

    public void setCrated_at(Date crated_at) {
        this.crated_at = crated_at;
    }

    public Date getTrip_date() {
        return trip_date;
    }

    public void setTrip_date(Date trip_date) {
        this.trip_date = trip_date;
    }

    public String getTrip_type() {
        return trip_type;
    }

    public void setTrip_type(String trip_type) {
        this.trip_type = trip_type;
    }

    public int getDeparture_hours() {
        return departure_hours;
    }

    public void setDeparture_hours(int departure_hours) {
        this.departure_hours = departure_hours;
    }

    public int getDeparture_minutes() {
        return departure_minutes;
    }

    public void setDeparture_minutes(int departure_minutes) {
        this.departure_minutes = departure_minutes;
    }
}
