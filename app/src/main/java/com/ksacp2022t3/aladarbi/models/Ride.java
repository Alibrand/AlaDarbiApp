package com.ksacp2022t3.aladarbi.models;

import java.util.Date;
import java.util.List;

public class Ride {
    String id;
    String trip_id;
    String driver_id;
    String driver_name;
    int reserved_seats;
    int free_seats;
    double price;
    Date ride_date;
    List<String> clients;
    String status="متاح";

    public Ride() {
    }

    public String getId() {
        return id;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public void setDriver_name(String driver_name) {
        this.driver_name = driver_name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String trip_id) {
        this.trip_id = trip_id;
    }

    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String driver_id) {
        this.driver_id = driver_id;
    }

    public int getReserved_seats() {
        return reserved_seats;
    }

    public void setReserved_seats(int reserved_seats) {
        this.reserved_seats = reserved_seats;
    }

    public int getFree_seats() {
        return free_seats;
    }

    public void setFree_seats(int free_seats) {
        this.free_seats = free_seats;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public Date getRide_date() {
        return ride_date;
    }

    public void setRide_date(Date ride_date) {
        this.ride_date = ride_date;
    }

    public List<String> getClients() {
        return clients;
    }

    public void setClients(List<String> clients) {
        this.clients = clients;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
