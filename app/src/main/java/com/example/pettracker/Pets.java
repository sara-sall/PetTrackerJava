package com.example.pettracker;

public class Pets {
    String name;
    String breederName;
    int birthday;
    int birthMonth;
    int birthYear;
    String race;
    String insurance;
    String insuranceNR;
    String other;
    String imageId;
    String petId;

    public Pets(String name, String breederName, int birthday, int birthMonth, int birthYear, String race, String insurance, String insuranceNR, String other, String imageId, String petId) {
        this.name = name;
        this.breederName = breederName;
        this.birthday = birthday;
        this.birthMonth = birthMonth;
        this.birthYear = birthYear;
        this.race = race;
        this.insurance = insurance;
        this.insuranceNR = insuranceNR;
        this.other = other;
        this.imageId = imageId;
        this.petId = petId;
    }

    public Pets() {
    }

    public int getBirthday() {
        return birthday;
    }

    public void setBirthday(int birthday) {
        this.birthday = birthday;
    }

    public int getBirthMonth() {
        return birthMonth;
    }

    public void setBirthMonth(int birthMonth) {
        this.birthMonth = birthMonth;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public void setBirthYear(int birthYear) {
        this.birthYear = birthYear;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBreederName() {
        return breederName;
    }

    public void setBreederName(String breederName) {
        this.breederName = breederName;
    }

    public String getRace() {
        return race;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getInsurance() {
        return insurance;
    }

    public void setInsurance(String insurance) {
        this.insurance = insurance;
    }

    public String getInsuranceNR() {
        return insuranceNR;
    }

    public void setInsuranceNR(String insuranceNR) {
        this.insuranceNR = insuranceNR;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getPetId() {
        return petId;
    }

    public void setPetId(String petId) {
        this.petId = petId;
    }
}
