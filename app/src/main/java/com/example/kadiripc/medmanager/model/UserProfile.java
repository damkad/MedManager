package com.example.kadiripc.medmanager.model;

/**
 * Created by kADIRI PC on 4/19/2018.
 */

public class UserProfile {
    String profileName;
    String profileCountry;
    String profileEmail;
    String profileHobby;
    String profileBirthday;
    String userId;
    String id;
    String profileNumber;

public UserProfile(){}

    public UserProfile(String profileName, String profileCountry,
                       String profileEmail, String profileHobby,
                       String profileBirthday, String userId, String id, String profilePhoneNumber) {
        this.profileName = profileName;
        this.profileCountry = profileCountry;
        this.profileEmail = profileEmail;
        this.profileHobby = profileHobby;
        this.profileBirthday = profileBirthday;
        this.userId = userId;
        this.id = id;
        this.profileNumber = profilePhoneNumber;
    }

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getProfileCountry() {
        return profileCountry;
    }

    public void setProfileCountry(String profileCountry) {
        this.profileCountry = profileCountry;
    }

    public String getProfileEmail() {
        return profileEmail;
    }

    public void setProfileEmail(String profileEmail) {
        this.profileEmail = profileEmail;
    }

    public String getProfileHobby() {
        return profileHobby;
    }

    public void setProfileHobby(String profileHobby) {
        this.profileHobby = profileHobby;
    }

    public String getProfileBirthday() {
        return profileBirthday;
    }

    public void setProfileBirthday(String profileBirthday) {
        this.profileBirthday = profileBirthday;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfileNumber() {
        return profileNumber;
    }

    public void setProfileNumber(String profileNumber) {
        this.profileNumber = profileNumber;
    }
}
