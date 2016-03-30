package com.montedigorno.votair.models;

/**
 * Created by brianhildebrand on 3/29/16.
 */
public class ProfileModel {

    private String firstName;
    private String lastName;
    private int houseNumber;
    private String streetName;
    private String state;
    private String zipCode;
    private String city;
    private int yearOfBirthCCYY;
    private int monthOfBirthMM;
    private int dayOfBirthDD;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getHouseNumber() {
        return houseNumber;
    }

    public void setHouseNumber(int houseNumber) {
        this.houseNumber = houseNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getYearOfBirthCCYY() {
        return yearOfBirthCCYY;
    }

    public void setYearOfBirthCCYY(int yearOfBirthCCYY) {
        this.yearOfBirthCCYY = yearOfBirthCCYY;
    }

    public int getMonthOfBirthMM() {
        return monthOfBirthMM;
    }

    public void setMonthOfBirthMM(int monthOfBirthMM) {
        this.monthOfBirthMM = monthOfBirthMM;
    }

    public int getDayOfBirthDD() {
        return dayOfBirthDD;
    }

    public void setDayOfBirthDD(int dayOfBirthDD) {
        this.dayOfBirthDD = dayOfBirthDD;
    }

}
