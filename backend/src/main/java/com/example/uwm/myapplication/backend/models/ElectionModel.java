package com.example.uwm.myapplication.backend.models;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by brianhildebrand on 3/29/16.
 */
@Entity
public class ElectionModel {


    @Id
    public Long id;

    @Index
    private String electionName;
    @Index
    private String electionDate;
    private String electionDateFormat = "MM-dd-yyyy";  //This will be in SimpleDateFormat, IE MM/dd/yyyy
    private ArrayList<ArrayList<String>> ballotItems;

    private List<String> ballotItems;

    private List<String> info;

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

    public List<String> getBallotItems() {
        return ballotItems;
    }

    public void setBallotItems(List<String> ballotItems) {
        this.ballotItems = ballotItems;
    }

    public List<String> getInfo() {
        return info;
    }

    public void setInfo(List<String> info) {
        this.info = info;
    }
}
