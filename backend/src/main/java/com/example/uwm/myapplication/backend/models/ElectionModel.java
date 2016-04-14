package com.example.uwm.myapplication.backend.models;

import java.util.ArrayList;

/**
 * Created by brianhildebrand on 3/29/16.
 */
public class ElectionModel {

    private String electionName;
    private String electionDate;
    private String electionDateFormat = "MM-dd-yyyy";  //This will be in SimpleDateFormat, IE MM/dd/yyyy
    private ArrayList<ArrayList<String>> ballotItems;

    public String getElectionName() {
        return electionName;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public String getElectionDateFormat() { return electionDateFormat; }

    public String getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(String electionDate) {
        this.electionDate = electionDate;
    }

    public ArrayList<ArrayList<String>> getBallotItems() {
        return ballotItems;
    }

    public void setBallotItems(ArrayList<ArrayList<String>> ballotItems) {
        this.ballotItems = ballotItems;
    }
}
