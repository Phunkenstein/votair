package com.example.uwm.myapplication.backend.models;

/**
 * Created by brianhildebrand on 4/21/16.
 */
public class InfoModel {

    private String pollingPlace;
    private String requiredDocumentation;
    private String restrictions;
    private String votingHoursStart;
    private String votingHoursEnd;

    public String getPollingPlace() {
        return pollingPlace;
    }

    public void setPollingPlace(String pollingPlace) {
        this.pollingPlace = pollingPlace;
    }

    public String getRequiredDocumentation() {
        return requiredDocumentation;
    }

    public void setRequiredDocumentation(String requiredDocumentation) {
        this.requiredDocumentation = requiredDocumentation;
    }

    public String getRestrictions() {
        return restrictions;
    }

    public void setRestrictions(String restrictions) {
        this.restrictions = restrictions;
    }


    public String getVotingHoursEnd() {
        return votingHoursEnd;
    }

    public void setVotingHoursEnd(String votingHoursEnd) {
        this.votingHoursEnd = votingHoursEnd;
    }

    public String getVotingHoursStart() {
        return votingHoursStart;
    }

    public void setVotingHoursStart(String votingHoursStart) {
        this.votingHoursStart = votingHoursStart;
    }
}