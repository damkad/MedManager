package com.example.kadiripc.medmanager.model;

import android.graphics.Bitmap;

/**
 * Created by kADIRI PC on 4/9/2018.
 */

public class medicine {

    String drug_name, time, from_date, to_date, presciption, photo;

    public medicine(){}

    public medicine(String drug_name, String time, String from_date, String to_date, String presciption, String photo) {
        this.drug_name = drug_name;
        this.time = time;
        this.from_date = from_date;
        this.to_date = to_date;
        this.presciption = presciption;
        this.photo = photo;
    }

    public String getDrug_name() {
        return drug_name;
    }

    public void setDrug_name(String drug_name) {
        this.drug_name = drug_name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
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
}
