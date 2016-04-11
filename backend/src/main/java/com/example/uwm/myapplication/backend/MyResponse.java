package com.example.uwm.myapplication.backend;

import java.util.List;

/**
 * Created by Chris Harmon on 3/27/2016.
 */
public class MyResponse {
    private List<Long> myData;
    private boolean success = false;



    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Long> getMyData() {
        return myData;
    }

    public void setMyData(List<Long> myData) {
        this.myData = myData;
    }
}
