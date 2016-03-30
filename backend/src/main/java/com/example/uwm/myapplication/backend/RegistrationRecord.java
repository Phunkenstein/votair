package com.example.uwm.myapplication.backend;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

/** The Objectify object model for device registrations we are persisting */
@Entity
public class RegistrationRecord {

    @Id
    Long id;

    @Index
    private String regId;

    // -1 indicates not to send any notifications.
    // 0 indicates send notification as soon as polling opens.
    // etc...
    @Index
    private int numHoursOut;

    public RegistrationRecord() { numHoursOut = -1; }

    public String getRegId() {
        return regId;
    }

    public void setRegId(String regId) {
        this.regId = regId;
    }

    public int getNumHoursOut() { return numHoursOut; }

    public void setNumHoursOut(int numDaysOut) { this.numHoursOut = numDaysOut; }
}