package com.montedigorno.votair.models;

import com.google.api.client.util.DateTime;

/**
 * Created by brianhildebrand on 4/21/16.
 */
public class InfoModel {

    private String pollingPlace;
    private String requiredDocumentation;
    private String restrictions;
    private String registrationDeadline;
    private String[] otherDeadlines;

    public DateTime getVotingHours() {
        return votingHours;
    }

    public void setVotingHours(DateTime votingHours) {
        this.votingHours = votingHours;
    }

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

    public String getRegistrationDeadline() {
        return registrationDeadline;
    }

    public void setRegistrationDeadline(String registrationDeadline) {
        this.registrationDeadline = registrationDeadline;
    }

    public String[] getOtherDeadlines() {
        return otherDeadlines;
    }

    public void setOtherDeadlines(String[] otherDeadlines) {
        this.otherDeadlines = otherDeadlines;
    }

    private DateTime votingHours;


}
