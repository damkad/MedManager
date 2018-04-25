package com.example.kadiripc.medmanager.model;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by kADIRI PC on 4/9/2018.
 */

public class medicine {

    String drug_name, from_date, to_date, presciption, photo;
    int time;

    long timestamp;

    public medicine() {
    }

    public medicine(String drug_name, int time, String from_date, String to_date, String presciption, String photo, long timestamp) {
        this.drug_name = drug_name;
        this.time = time;
        this.from_date = from_date;
        this.to_date = to_date;
        this.presciption = presciption;
        this.photo = photo;
        this.timestamp = timestamp;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getFrom_date() {
        return from_date;
    }

    public void setFrom_date(String from_date) {
        this.from_date = from_date;
    }

    public String getTo_date() {
        return to_date;
    }

    public void setTo_date(String to_date) {
        this.to_date = to_date;
    }

    public String getPresciption() {
        return presciption;
    }

    public void setPresciption(String presciption) {
        this.presciption = presciption;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }



}
