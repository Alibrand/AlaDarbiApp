package com.ksacp2022t3.aladarbi.models;

import java.util.Date;

public class Reservation {
    String id;
    String trip_id;
    String driver_id;
    String client_name;
    String client_id;
    Date reservation_date;
    boolean paid=false;

    public Reservation() {
    }

    public String getId() {
        return id;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
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

    public String getClient_name() {
        return client_name;
    }

    public void setClient_name(String client_name) {
        this.client_name = client_name;
    }

    public String getClient_id() {
        return client_id;
    }

    public void setClient_id(String client_id) {
        this.client_id = client_id;
    }

    public Date getReservation_date() {
        return reservation_date;
    }

    public void setReservation_date(Date reservation_date) {
        this.reservation_date = reservation_date;
    }
}
