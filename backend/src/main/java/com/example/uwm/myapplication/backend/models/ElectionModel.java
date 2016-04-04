package com.example.uwm.myapplication.backend.models;


// One or the other I guess?
//import com.google.appengine.repackaged.com.google.api.client.util.DateTime;
import com.google.appengine.repackaged.org.joda.time.DateTime;

/**
 * Created by brianhildebrand on 3/29/16.
 */
public class ElectionModel {

    private String electionName;
    private DateTime electionDate;

    public String getElectionName() {
        return electionName;
    }

    public void setElectionName(String electionName) {
        this.electionName = electionName;
    }

    public DateTime getElectionDate() {
        return electionDate;
    }

    public void setElectionDate(DateTime electionDate) {
        this.electionDate = electionDate;
    }
}
